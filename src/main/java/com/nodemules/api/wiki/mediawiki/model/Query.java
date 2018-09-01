package com.nodemules.api.wiki.mediawiki.model;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import lombok.Data;

@Data
public class Query implements Serializable {

  private static final long serialVersionUID = 1771741393261897279L;

  private Map<String, Page> pages = new HashMap<>();
  private List<Result> random = new ArrayList<>();
}
