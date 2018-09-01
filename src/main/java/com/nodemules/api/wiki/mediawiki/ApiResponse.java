package com.nodemules.api.wiki.mediawiki;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.nodemules.api.wiki.mediawiki.model.Parse;
import com.nodemules.api.wiki.mediawiki.model.Query;
import java.io.Serializable;
import lombok.Data;

@Data
public class ApiResponse implements Serializable {

  private static final long serialVersionUID = -2870515801365165733L;

  @JsonProperty("continue")
  private Continue page;
  private Parse parse;
  private Query query;

  @Data
  public class Continue {

    @JsonProperty("gplcontinue")
    private String generatorContinue;
    @JsonProperty("continue")
    private String continueExpression;
  }
}
