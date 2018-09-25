package com.nodemules.api.wiki.core.article.pojo;

import java.io.Serializable;
import lombok.Data;

@Data
public class ArticleEntityPojo implements Serializable {

  private Long articleId;
  private String articleName;
  private String articleText;

}
