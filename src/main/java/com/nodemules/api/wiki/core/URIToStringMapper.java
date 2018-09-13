package com.nodemules.api.wiki.core;

import java.net.URI;
import org.springframework.stereotype.Component;

@Component
public class URIToStringMapper {

  public String toString(URI uri) {
    if (uri == null) {
      return null;
    }
    return uri.toString();
  }
}
