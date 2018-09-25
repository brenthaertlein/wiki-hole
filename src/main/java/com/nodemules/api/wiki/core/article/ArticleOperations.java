package com.nodemules.api.wiki.core.article;

import com.nodemules.api.wiki.persistence.domain.ArticleEntity;

public interface ArticleOperations {

  ArticleEntity get(Long articleId);

  ArticleEntity getByName(String articleName);
}
