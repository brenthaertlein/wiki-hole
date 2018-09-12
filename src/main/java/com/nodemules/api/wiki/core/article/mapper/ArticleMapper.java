package com.nodemules.api.wiki.core.article.mapper;

import com.nodemules.api.wiki.core.article.model.ArticleModel;
import com.nodemules.api.wiki.core.article.pojo.Article;
import fr.xebia.extras.selma.IoC;
import fr.xebia.extras.selma.Mapper;
import fr.xebia.extras.selma.Maps;

@Mapper(withIoC = IoC.SPRING)
public interface ArticleMapper {

  @Maps(
      withIgnoreFields = {
          "href"
      }
  )
  Article toPojo(ArticleModel model);
}
