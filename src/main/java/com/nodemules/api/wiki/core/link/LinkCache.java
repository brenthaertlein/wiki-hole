package com.nodemules.api.wiki.core.link;

import com.nodemules.api.wiki.mediawiki.model.Page;
import com.nodemules.cache.core.Cache;

public class LinkCache extends Cache<String, Page> {

  private final Cache<String, Page> cache = Cache.<String, Page>builder().build();
}
