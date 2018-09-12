package com.nodemules.api.wiki.core.article.pojo;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonInclude.Include;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;
import lombok.Data;

@Data
@JsonInclude(Include.NON_EMPTY)
public class ArticleTrace implements Serializable {

  private static final long serialVersionUID = -2022063429367229329L;

  private int pageId;
  private int revisionId;
  private int namespaceId;

  private String title;
  private Article first;
  private Article last;
  private Article endOfChain;

  private List<Article> articleChain = new ArrayList<>();

  public int getChainDepth() {
    return articleChain.size();
  }
}
