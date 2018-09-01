package com.nodemules.api.wiki.mediawiki.model;

import java.io.Serializable;
import lombok.Data;

@Data
public class Redirect implements Serializable {

  private static final long serialVersionUID = 3753762538096778841L;
  private String from;
  private String to;
}
