package com.nodemules.api.wiki.persistence.repository;

import com.nodemules.api.wiki.persistence.domain.ArticleEntity;
import org.springframework.data.jpa.repository.JpaRepository;

public interface ArticleRepository extends JpaRepository<ArticleEntity, Long> {

}
