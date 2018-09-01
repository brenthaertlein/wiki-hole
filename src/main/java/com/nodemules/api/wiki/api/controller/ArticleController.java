package com.nodemules.api.wiki.api.controller;

import com.nodemules.api.wiki.core.article.pojo.ArticleTrace;
import com.nodemules.api.wiki.mediawiki.MediaWikiApiClient;
import com.nodemules.api.wiki.mediawiki.model.Article;
import com.nodemules.api.wiki.mediawiki.model.Link;
import com.nodemules.api.wiki.mediawiki.model.Page;
import com.nodemules.api.wiki.mediawiki.model.Parse;
import com.nodemules.api.wiki.mediawiki.model.Redirect;
import com.nodemules.api.wiki.mediawiki.model.Result;
import com.nodemules.cache.core.Cache;
import com.nodemules.cache.core.CachedRecord;
import java.util.Collections;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import lombok.extern.slf4j.Slf4j;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@Slf4j
@RestController
@RequestMapping("/article")
public class ArticleController {

  private final Map<String, Page> linkCache = new ConcurrentHashMap<>();
  private final Map<Integer, Article> articleCache = new ConcurrentHashMap<>();

  @GetMapping("")
  public ArticleTrace traceArticle() {
    Result randomPage = MediaWikiApiClient.random();

    return getArticleTrace(randomPage.getId());
  }

  @GetMapping("/{id}")
  public ArticleTrace traceArticle(@PathVariable int id) {
    return getArticleTrace(id);
  }

  private ArticleTrace getArticleTrace(int pageId) {
    MultiValueMap<String, String> params = new LinkedMultiValueMap<>();
    params.add("section", "0");
    ArticleTrace articleTrace = new ArticleTrace();
    Parse parsed = MediaWikiApiClient.parse(pageId, params);
    if (parsed == null) {
      return null;
    }
    articleTrace.setTitle(parsed.getTitle());
    articleTrace.setPageId(parsed.getPageId());
    Article firstLink = parsed.getText().getFirstArticle();
    List<Page> links = MediaWikiApiClient.links(parsed.getPageId());
    firstLink.setPageId(
        links.stream().filter(p -> firstLink.getTitle().equals(p.getTitle())).findFirst()
            .orElse(new Page()).getPageId());
    articleTrace.setArticleChain(getArticleChain(new LinkedList<>(
        Collections.singletonList(firstLink))));
    return articleTrace;
  }

  private LinkedList<Article> getArticleChain(LinkedList<Article> list) {

    Article last = list.getLast();

    final Article fromCache = articleCache.getOrDefault(last.getPageId(), null);
    if (fromCache != null && fromCache.getNext() != null && list.stream()
        .noneMatch(a -> a.getPageId() == fromCache.getNext().getPageId())) {
      log.info("Retrieved article {} from cache", fromCache.getNext().getTitle());
      list.add(fromCache.getNext());
      return getArticleChain(list);
    }

    MultiValueMap<String, String> params = new LinkedMultiValueMap<>();
    params.add("section", "0");
    Parse parsed = MediaWikiApiClient.parse(last.getPageId(), params);
    Article nextArticle = parsed.getText().getFirstArticle();
    if (nextArticle == null) {
      return list;
    }

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
        Page page = new Page();
        page.setPageId(last.getPageId());
        page.setTitle(last.getTitle());
        last.setRedirectedFrom(page);
        last.setPageId(redirect.getPageId());
        last.setTitle(redirect.getTitle());
      }
    }

    Page next = getNextPage(last.getPageId(), nextArticle.getTitle());
    if (next == null) {

      // TODO - fetch more links if links result is pageable
      log.debug("Unable to find next link to {} from {}", nextArticle.getTitle(), last.getTitle());
      return list;
    }
    Article article = new Article();
    article.setPageId(next.getPageId());
    article.setTitle(next.getTitle());
    article.setHref(nextArticle.getHref());

    Article next2 = new Article();
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
      log.info("Retrieved page {} from cache", title);
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
