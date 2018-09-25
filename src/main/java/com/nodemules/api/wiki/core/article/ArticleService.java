package com.nodemules.api.wiki.core.article;

import com.nodemules.api.wiki.persistence.domain.ArticleEntity;
import com.nodemules.api.wiki.persistence.repository.ArticleRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class ArticleService implements ArticleOperations {
  private final ArticleRepository articleRepository;

  @Override
  public ArticleEntity get(Long articleId) {
    return articleRepository.getOne(articleId);
  }

  @Override
  public ArticleEntity getByName(String articleName) {
    ArticleEntity byArticleName = articleRepository.findByArticleName(articleName);
    return byArticleName;
  }
}
