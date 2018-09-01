package com.nodemules.api.wiki.mediawiki.model;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.databind.PropertyNamingStrategy;
import com.fasterxml.jackson.databind.annotation.JsonNaming;
import java.io.Serializable;
import lombok.Data;

@Data
@JsonNaming(PropertyNamingStrategy.LowerCaseStrategy.class)
public class Result implements Serializable {

  private static final long serialVersionUID = 4998055178210663745L;

  private String title;
  private int id;

  @JsonProperty("ns")
  private int namespace;
}
