package com.nodemules.api.wiki.core.article.model;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonInclude.Include;
import java.util.LinkedList;
import lombok.Data;

@Data
@JsonInclude(Include.NON_EMPTY)
public class ArticleTraceModel {

  private int pageId;
  private int revisionId;
  private int namespaceId;

  private String title;

  private LinkedList<ArticleModel> articleChain = new LinkedList<>();

  public ArticleModel getFirst() {
    return articleChain.getFirst();
  }

  public ArticleModel getLast() {
    return articleChain.getLast();
  }

  public ArticleModel getEndOfChain() {
    if (this.getLast() == null) {
      return null;
    }
    return this.getLast().getNext();
  }

  public int getChainDepth() {
    return articleChain.size();
  }
}
