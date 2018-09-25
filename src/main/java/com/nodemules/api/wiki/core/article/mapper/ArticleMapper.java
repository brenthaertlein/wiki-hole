package com.nodemules.api.wiki.core.article.mapper;

import com.nodemules.api.wiki.core.URIToStringMapper;
import com.nodemules.api.wiki.core.article.model.ArticleModel;
import com.nodemules.api.wiki.core.article.pojo.Article;
import com.nodemules.api.wiki.core.article.pojo.ArticleEntityPojo;
import com.nodemules.api.wiki.persistence.domain.ArticleEntity;
import fr.xebia.extras.selma.IoC;
import fr.xebia.extras.selma.Mapper;
import fr.xebia.extras.selma.Maps;

@Mapper(withIoC = IoC.SPRING, withCustom = {URIToStringMapper.class})
public interface ArticleMapper {

  Article toPojo(ArticleModel model);

  @Maps(withIgnoreFields = {"namespaceId"})
  ArticleEntityPojo toPojo(ArticleEntity entity);
}
