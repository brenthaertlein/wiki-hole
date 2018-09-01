package com.nodemules.api.wiki.mediawiki;

import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public enum Params {
  ACTION("action"),
  PAGE_ID("pageid"),
  PAGE_IDS("pageids"),
  FORMAT("format"),
  PROP("prop"),
  GENERATOR("generator"),
  LIST("list"),
  REDIRECTS("redirects");

  private final String value;
}
