package com.nodemules.api.wiki.mediawiki.model;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonInclude.Include;
import java.net.URI;
import lombok.Data;
import lombok.EqualsAndHashCode;

@Data
@EqualsAndHashCode(callSuper = true)
@JsonInclude(Include.NON_EMPTY)
public class Article extends Page {

  private static final long serialVersionUID = -1870227098539230056L;

  private URI href;
  private Article next;
  private Page redirectedFrom;
}
