package com.nodemules.api.wiki.persistence.domain;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.Table;
import lombok.Data;

@Data
@Entity
@Table(name="article", schema="wikipedia")
public class ArticleEntity {
  @Id
  @Column(name="article_id")
  private Long articleId;
  private String articleName;
  private Integer namespaceId;
}
