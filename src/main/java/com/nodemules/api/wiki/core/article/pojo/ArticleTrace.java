package com.nodemules.api.wiki.core.article.pojo;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonInclude.Include;
import com.nodemules.api.wiki.mediawiki.model.Article;
import java.io.Serializable;
import java.util.LinkedList;
import lombok.Data;

@Data
@JsonInclude(Include.NON_EMPTY)
public class ArticleTrace implements Serializable {

  private static final long serialVersionUID = -2022063429367229329L;

  private int pageId;
  private int revisionId;
  private int namespaceId;

  private String title;

  private LinkedList<Article> articleChain = new LinkedList<>();

  public Article getFirst() {
    return articleChain.getFirst();
  }

  public Article getLast() {
    return articleChain.getLast();
  }

  public Article getEndOfChain() {
    if (this.getLast() == null) {
      return null;
    }
    return this.getLast().getNext();
  }

  public int getChainDepth() {
    return articleChain.size();
  }
}
