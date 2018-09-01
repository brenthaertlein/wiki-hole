package com.nodemules.api.wiki.mediawiki.model;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.databind.PropertyNamingStrategy;
import com.fasterxml.jackson.databind.annotation.JsonNaming;
import java.io.Serializable;
import lombok.Data;

@Data
@JsonNaming(PropertyNamingStrategy.LowerCaseStrategy.class)
public class Page implements Serializable {

  private static final long serialVersionUID = -1001422587897005850L;

  private String title;
  private int pageId;

  @JsonProperty("ns")
  private int namespace;
}
