package com.nodemules.api.wiki.core.article;

import com.nodemules.api.wiki.core.article.model.ArticleModel;
import com.nodemules.api.wiki.core.article.model.ArticleTraceModel;
import com.nodemules.api.wiki.core.article.pojo.Article;
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
    MultiValueMap<String, String> params = new LinkedMultiValueMap<>();
    params.add("section", "0");
    ArticleTraceModel model = new ArticleTraceModel();
    Parse parsed = MediaWikiApiClient.parse(pageId, params);
    if (parsed == null) {
      return null;
    }
    model.setTitle(parsed.getTitle());
    model.setPageId(parsed.getPageId());
    ArticleModel firstLink = ArticleParser.getFirstArticle(parsed.getText().getValue());
    if (firstLink == null) {
      return model;
    }
    List<Page> links = MediaWikiApiClient.links(parsed.getPageId());
    firstLink.setPageId(
        links.stream().filter(p -> firstLink.getTitle().equals(p.getTitle())).findFirst()
            .orElse(new Page()).getPageId());
    model
        .setArticleChain(getArticleChain(new LinkedList<>(Collections.singletonList(firstLink))));

    return model;
  }

  private LinkedList<ArticleModel> getArticleChain(LinkedList<ArticleModel> list) {

    ArticleModel last = list.getLast();

    if (last.getNext() != null) {
      final ArticleModel fromCache = articleCache.getOrDefault(last.getNext().getPageId(), null);
      if (fromCache != null && list.stream()
          .noneMatch(a -> a.getPageId() == fromCache.getPageId())) {
        log.info("Retrieved article {} from cache", fromCache.getTitle());
        list.add(fromCache);
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
        ArticleModel article = new ArticleModel();
        article.setPageId(last.getPageId());
        article.setTitle(last.getTitle());
        last.setRedirectedFrom(article);
        last.setPageId(redirect.getPageId());
        last.setTitle(redirect.getTitle());
      }
    }

    ArticleModel nextArticle = ArticleParser.getFirstArticle(parsed.getText().getValue());
    if (nextArticle == null) {
      return list;
    }

    Page next = getNextPage(last.getPageId(), nextArticle.getTitle());
    if (next == null) {

      // TODO - fetch more links if links result is pageable
      log.debug("Unable to find next link to {} from {}", nextArticle.getTitle(), last.getTitle());
      return list;
    }
    ArticleModel article = new ArticleModel();
    article.setPageId(next.getPageId());
    article.setTitle(next.getTitle());
    article.setHref(nextArticle.getHref());

    ArticleModel next2 = new ArticleModel();
    next2.setPageId(next.getPageId());
    next2.setTitle(next.getTitle());
    next2.setHref(nextArticle.getHref());
    last.setNext(next2);

    articleCache.put(last.getPageId(), last);

    int nextPageId = article.getPageId();
    if (list.stream().noneMatch(a -> a.getPageId() == nextPageId)) {
      list.add(article);
      return getArticleChain(list);
    }
    return list;
  }

  private Page getNextPage(int pageId, String title) {
    Page page = linkCache.getOrDefault(title, null);
    if (page != null) {
      log.info("Retrieved link to page {} from cache", title);
      return page;
    }
    List<Page> links = MediaWikiApiClient.links(pageId);

    for (Page p : links) {
      linkCache.put(p.getTitle(), p);
      if (p.getTitle().equals(title)) {
        return p;
      }

    }
    return null;
  }

  private Article getArticle(int pageId) {
    MultiValueMap<String, String> params = new LinkedMultiValueMap<>();
    params.add("section", "0");
    Parse parsed = MediaWikiApiClient.parse(pageId, params);
    if (parsed == null) {
      return null;
    }

    Article article = new Article();

    article.setPageId(parsed.getPageId());
    article.setTitle(parsed.getTitle());
    return article;
  }
}
