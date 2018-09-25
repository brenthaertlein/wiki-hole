package com.nodemules.api.wiki.api.controller;

import com.nodemules.api.wiki.core.article.ArticleOperations;
import com.nodemules.api.wiki.core.article.ArticleTraceOperations;
import com.nodemules.api.wiki.core.article.mapper.ArticleMapper;
import com.nodemules.api.wiki.core.article.mapper.ArticleTraceMapper;
import com.nodemules.api.wiki.core.article.model.ArticleTraceModel;
import com.nodemules.api.wiki.core.article.pojo.ArticleEntityPojo;
import com.nodemules.api.wiki.core.article.pojo.ArticleTrace;
import com.nodemules.api.wiki.persistence.domain.ArticleEntity;
import com.nodemules.mediawiki.MediaWikiApiClient;
import com.nodemules.mediawiki.model.Result;
import java.util.ArrayList;
import java.util.List;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@Slf4j
@RestController
@RequestMapping("/article")
@RequiredArgsConstructor(onConstructor = @__(@Autowired))
public class ArticleController {

  private final ArticleMapper articleMapper;
  private final ArticleTraceMapper articleTraceMapper;
  private final ArticleTraceOperations articleTraceService;
  private final ArticleOperations articleService;

  @GetMapping("/{id}")
  public ArticleEntityPojo getArticle(@PathVariable Long id) {
    return articleMapper.toPojo(articleService.get(id));
  }

  @GetMapping("/query")
  public ArticleEntityPojo getArticleByName(@RequestParam(name="name") String articleName) {
    ArticleEntity entity = articleService.getByName(articleName);
    return articleMapper.toPojo(entity);
  }

  @GetMapping("/trace")
  public ArticleTrace traceArticle() {
    Result randomPage = MediaWikiApiClient.random();

    return articleTraceMapper.toPojo(articleTraceService.getArticleTrace(randomPage.getId()));
  }

  @GetMapping("/trace/{id}")
  public ArticleTrace traceArticle(@PathVariable int id) {
    return articleTraceMapper.toPojo(articleTraceService.getArticleTrace(id));
  }

  @GetMapping("/trace/list")
  public List<ArticleTrace> traceArticles(
      @RequestParam(required = false, defaultValue = "10") int number) {
    List<ArticleTraceModel> list = new ArrayList<>();
    for (int i = 0; i < number; i++) {
      Result randomPage = MediaWikiApiClient.random();
      list.add(articleTraceService.getArticleTrace(randomPage.getId()));
    }
    return articleTraceMapper.toPojos(list);
  }

}
