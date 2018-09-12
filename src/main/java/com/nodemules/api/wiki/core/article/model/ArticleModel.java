package com.nodemules.api.wiki.core.article.model;

import java.net.URI;
import lombok.Data;

@Data
public class ArticleModel {

  private URI href;
  private ArticleModel next;
  private String title;
  private int pageId;
  private int namespaceId;
  private ArticleModel redirectedFrom;

}
