package com.nodemules.api.wiki.mediawiki;

import com.nodemules.api.wiki.mediawiki.model.Page;
import com.nodemules.api.wiki.mediawiki.model.Parse;
import com.nodemules.api.wiki.mediawiki.model.Result;
import java.net.URI;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Component;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.util.UriComponentsBuilder;

@Slf4j
@Component
public class MediaWikiApiClient {

  private static final String BASE_URI = "https://en.wikipedia.org/w/api.php";

  private static final RestTemplate REST_TEMPLATE = new RestTemplate();

  public static Result random() {
    List<Result> results = random(1);
    return results.stream().findFirst().orElse(null);
  }

  public static List<Result> random(int limit) {
    MultiValueMap<String, String> params = new LinkedMultiValueMap<>();
    params.add(Params.ACTION.getValue(), Action.QUERY.getValue());
    params.add(Params.LIST.getValue(), "random");
    params.add("rnlimit", String.valueOf(Math.max(0, Math.min(20, limit))));
    params.add("rnnamespace", String.valueOf(0));
    params.add(Params.FORMAT.getValue(), "json");
    ApiResponse apiResponse = query(params);
    return apiResponse.getQuery().getRandom();
  }

  public static Parse parse(int pageId) {
    return parse(pageId, new LinkedMultiValueMap<>());
  }

  public static Parse parse(int pageId, MultiValueMap<String, String> additionalParams) {
    return parse(pageId, additionalParams, false);
  }

  public static Parse parse(int pageId, MultiValueMap<String, String> additionalParams,
      boolean followRedirect) {
    MultiValueMap<String, String> params = new LinkedMultiValueMap<>();
    params.add(Params.ACTION.getValue(), Action.PARSE.getValue());
    params.add(Params.FORMAT.getValue(), "json");
    params.add(Params.PAGE_ID.getValue(), String.valueOf(pageId));
    List<String> props = new ArrayList<>();
    props.add("text");
    props.add("wikitext");
    props.add("revid");
    props.add("links");
    params.add(Params.PROP.getValue(), String.join("|", props));
    if (followRedirect) {
      params.add(Params.REDIRECTS.getValue(), Boolean.TRUE.toString());
    }
    params.addAll(additionalParams);

    ApiResponse apiResponse = query(params);

    Parse parsed = apiResponse.getParse();

    if (parsed.getWikiText().getValue().contains("#REDIRECT")) {
      return parse(pageId, additionalParams, true);
    }

    return parsed;
  }

  public static List<Page> links(int pageId) {
    return links(pageId, new LinkedMultiValueMap<>());
  }

  public static List<Page> links(int pageId, MultiValueMap<String, String> additionalParams) {
    MultiValueMap<String, String> params = new LinkedMultiValueMap<>();
    params.add(Params.ACTION.getValue(), Action.QUERY.getValue());
    params.add(Params.FORMAT.getValue(), "json");
    params.add(Params.PAGE_IDS.getValue(), String.valueOf(pageId));
    params.add(Params.GENERATOR.getValue(), Generators.LINKS.getValue());
    params.add("gpllimit", "max");
    params.addAll(additionalParams);

    ApiResponse apiResponse = query(params);
    Map<String, Page> map = apiResponse.getQuery().getPages();
    List<Page> pages = new ArrayList<>(map.values());

    // TODO - return response and allow calling next page from response object?
    if (apiResponse.getPage() != null) {
      MultiValueMap<String, String> moreParams = new LinkedMultiValueMap<>();
      moreParams.add("gplcontinue", apiResponse.getPage().getGeneratorContinue());
      pages.addAll(links(pageId, moreParams));
    }

    return pages;
  }

  private static ApiResponse query(MultiValueMap<String, String> params) {
    URI uri = UriComponentsBuilder.fromUriString(BASE_URI).queryParams(params).build().toUri();
    log.debug("Making request to {}", uri.toString());
    ResponseEntity<ApiResponse> response = REST_TEMPLATE.getForEntity(uri, ApiResponse.class);
    return response.getBody();
  }
}
