package com.nodemules.api.wiki.core.article.mapper;

import com.nodemules.api.wiki.core.article.model.ArticleTraceModel;
import com.nodemules.api.wiki.core.article.pojo.ArticleTrace;
import fr.xebia.extras.selma.IoC;
import fr.xebia.extras.selma.Mapper;
import fr.xebia.extras.selma.Maps;
import java.util.List;

@Mapper(
    withIoC = IoC.SPRING,
    withCustom = {
        ArticleMapper.class
    }
)
public interface ArticleTraceMapper {

  @Maps(
      withIgnoreFields = {
          "chainDepth"
      }
  )
  ArticleTrace toPojo(ArticleTraceModel model);

  List<ArticleTrace> toPojos(List<ArticleTraceModel> list);
}
