package com.nodemules.api.wiki.core.article.mapper;

import com.nodemules.api.wiki.core.URIToStringMapper;
import com.nodemules.api.wiki.core.article.model.ArticleModel;
import com.nodemules.api.wiki.core.article.pojo.Article;
import fr.xebia.extras.selma.IoC;
import fr.xebia.extras.selma.Mapper;

@Mapper(withIoC = IoC.SPRING, withCustom = {URIToStringMapper.class})
public interface ArticleMapper {

  Article toPojo(ArticleModel model);
}
