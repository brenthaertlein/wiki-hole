package com.nodemules.api.wiki.core.article.pojo;

import java.io.Serializable;
import lombok.Data;

@Data
public class Article implements Serializable {

  private static final long serialVersionUID = -4670573566323394797L;

  private String href;
  private Article next;
  private String title;
  private int pageId;
  private int namespaceId;
  private Article redirectedFrom;
}
