package com.nodemules.api.wiki.core.article;

import com.nodemules.api.wiki.core.article.model.ArticleModel;
import com.nodemules.api.wiki.core.article.model.ArticleTraceModel;
import com.nodemules.mediawiki.MediaWikiApiClient;
import com.nodemules.mediawiki.model.Page;
import com.nodemules.mediawiki.model.Parse;
import com.nodemules.mediawiki.model.Redirect;
import java.util.Collections;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;

@Slf4j
@Service
public class ArticleTraceService implements ArticleTraceOperations {

  private final Map<String, Page> linkCache = new ConcurrentHashMap<>();
  private final Map<Integer, ArticleModel> articleCache = new ConcurrentHashMap<>();

  @Override
  public ArticleTraceModel getArticleTrace(int pageId) {
    ArticleModel article = getArticle(pageId);
    if (article == null) {
      return null;
    }
    ArticleTraceModel model = new ArticleTraceModel();
    model.setTitle(article.getTitle());
    model.setPageId(article.getPageId());
    model.setArticleChain(
        getArticleChain(new LinkedList<>(Collections.singletonList(article.getNext()))));

    return model;
  }

  private LinkedList<ArticleModel> getArticleChain(LinkedList<ArticleModel> list) {

    ArticleModel last = list.getLast();

    if (last.getNext() != null) {
      ArticleModel next = getArticle(last.getNext().getPageId());
      if (next != null) {
        if (list.stream()
            .anyMatch(a -> a.getPageId() == next.getPageId())) {
          return list;
        }

        last.setNext(next.clone());
        list.add(next);
        return getArticleChain(list);
      }
    }

    MultiValueMap<String, String> params = new LinkedMultiValueMap<>();
    params.add("section", "0");
    Parse parsed = MediaWikiApiClient.parse(last.getPageId(), params);

    if (!parsed.getRedirects().isEmpty()) {
      List<Page> redirectLinks = MediaWikiApiClient.links(parsed.getPageId());
      Page redirect = null;
      for (Redirect r : parsed.getRedirects()) {
        for (Page page : redirectLinks) {
          if (page.getTitle().equals(r.getTo())) {
            redirect = page;
            break;
          }
        }
      }
      if (redirect != null) {
        last.setRedirectedFrom(last.clone());
        last.setPageId(redirect.getPageId());
        last.setTitle(redirect.getTitle());
      }
    }

    ArticleModel nextArticle = ArticleParser.getFirstArticle(parsed.getText().getValue());
    if (nextArticle == null) {
      return list;
    }

    ArticleModel next = getNextPage(last.getPageId(), nextArticle.getTitle());
    if (next == null) {
      // TODO - fetch more links if links result is pageable
      log.debug("Unable to find next link to {} from {}", nextArticle.getTitle(), last.getTitle());
      return list;
    }

    final ArticleModel fromCache = getArticle(next.getPageId());
    if (fromCache != null) {
      if (list.stream()
          .anyMatch(a -> a.getPageId() == fromCache.getPageId())) {
        return list;
      }

      last.setNext(next.clone());
      list.add(fromCache);
      return getArticleChain(list);
    }

    ArticleModel article = new ArticleModel();
    article.setPageId(next.getPageId());
    article.setTitle(next.getTitle());
    article.setHref(nextArticle.getHref());
    last.setNext(article.clone());

    articleCache.putIfAbsent(last.getPageId(), last.clone());

    int nextPageId = article.getPageId();
    if (list.stream().noneMatch(a -> a.getPageId() == nextPageId)) {
      list.add(article);
      return getArticleChain(list);
    }
    return list;
  }

  private ArticleModel getNextPage(int pageId, String title) {
    Page page = linkCache.getOrDefault(title, null);
    if (page != null) {
      log.info("Retrieved link to page {} from cache", title);
      ArticleModel a = new ArticleModel();
      a.setPageId(page.getPageId());
      a.setNamespaceId(page.getNamespace());
      a.setTitle(page.getTitle());
      return a;
    }
    List<Page> links = MediaWikiApiClient.links(pageId);

    for (Page p : links) {
      linkCache.put(p.getTitle(), p);
      if (p.getTitle().equals(title)) {
        ArticleModel a = new ArticleModel();
        a.setPageId(p.getPageId());
        a.setNamespaceId(p.getNamespace());
        a.setTitle(p.getTitle());
        return a;
      }

    }
    return null;
  }

  private ArticleModel getArticle(int pageId) {
    MultiValueMap<String, String> params = new LinkedMultiValueMap<>();
    params.add("section", "0");

    final ArticleModel fromCache = articleCache.getOrDefault(pageId, null);
    if (fromCache != null) {
      log.info("Retrieved article {} from cache", fromCache.getTitle());
      return fromCache;
    }
    Parse parsed = MediaWikiApiClient.parse(pageId, params);
    if (parsed == null) {
      return null;
    }

    ArticleModel article = new ArticleModel();

    article.setPageId(parsed.getPageId());
    article.setTitle(parsed.getTitle());

    ArticleModel nextArticle = ArticleParser.getFirstArticle(parsed.getText().getValue());
    if (nextArticle != null) {
      ArticleModel next = getNextPage(pageId, nextArticle.getTitle());
      if (next != null) {
        next.setHref(nextArticle.getHref());
        article.setNext(next.clone());
      }
    }
    articleCache.putIfAbsent(article.getPageId(), article.clone());
    return article;
  }
}
