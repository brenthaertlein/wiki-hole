package com.nodemules.api.wiki.core.article.pojo;

import com.fasterxml.jackson.annotation.JsonProperty;
import java.net.URI;
import lombok.Data;

@Data
public class Article {

  private URI href;
  private Article next;
  private String title;
  private int pageId;
  private int namespaceId;
  private Article redirectedFrom;
}
