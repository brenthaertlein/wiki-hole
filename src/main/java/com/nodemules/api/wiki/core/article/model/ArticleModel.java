package com.nodemules.api.wiki.core.article.model;

import java.net.URI;
import java.util.ArrayList;
import java.util.List;
import lombok.Data;

@Data
public class ArticleModel implements Cloneable {

  private URI href;
  private ArticleModel next;
  private String title;
  private int pageId;
  private int namespaceId;
  private ArticleModel redirectedFrom;
  private List<ArticleModel> redirects = new ArrayList<>();

  @Override
  public ArticleModel clone() {
    ArticleModel clone;
    try {
      clone = (ArticleModel) super.clone();
    } catch (CloneNotSupportedException e) {
      return null;
    }
    clone.setTitle(this.title);
    clone.setHref(this.href);
    clone.setPageId(this.pageId);
    clone.setNamespaceId(this.namespaceId);
    if (this.next != null) {
      ArticleModel next = this.next.clone();
      next.setNext(null);
      clone.setNext(next);
    }
    if (this.redirectedFrom != null) {
      clone.setRedirectedFrom(this.redirectedFrom.clone());
    }
    return clone;
  }

}
