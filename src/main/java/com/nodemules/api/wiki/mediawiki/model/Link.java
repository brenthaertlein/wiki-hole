package com.nodemules.api.wiki.mediawiki.model;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Data;
import lombok.EqualsAndHashCode;

@Data
@EqualsAndHashCode(callSuper = true)
public class Link extends ValueHolder {

  private static final long serialVersionUID = 1343294457797550332L;

  @JsonProperty("ns")
  private int namespace;
}
