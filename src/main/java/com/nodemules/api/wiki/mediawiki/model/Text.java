package com.nodemules.api.wiki.mediawiki.model;

import java.net.URI;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.extern.slf4j.Slf4j;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.nodes.Node;
import org.jsoup.parser.Tag;
import org.jsoup.select.Elements;
import org.springframework.web.util.UriComponentsBuilder;

@Slf4j
@Data
@EqualsAndHashCode(callSuper = true)
public class Text extends ValueHolder {

  private static final List<String> validLinkParents = Arrays.asList("p", "li");
  private static final List<String> blacklistedUrlPatterns = Arrays
      .asList("Help:", "File:", "Wikipedia:", "Category:");

  private static final long serialVersionUID = 558999892219819016L;
  private static final String WIKIPEDIA_BASE_URL = "https://en.wikipedia.org/";

  public Article getFirstArticle() {
    Document doc = Jsoup.parse(this.value);
    Elements contentText = doc.select(".mw-parser-output");
    Elements noArticleText = doc.select(".noarticletext");
    if (contentText.isEmpty() && !noArticleText.isEmpty()) {
      return null;
    }
    Elements links = doc.select(".mw-parser-output > p a, .mw-parser-output > ul a");
    Element link = links.stream().filter(this::isArticleLink).findFirst().orElse(null);
    if (link == null) {
      return null;
    }
    Article article = new Article();
    URI href = UriComponentsBuilder.fromUriString(WIKIPEDIA_BASE_URL).path(link.attr("href"))
        .build().toUri();
    article.setTitle(link.attr("title"));
    article.setHref(href);
    return article;
  }

  private boolean isArticleLink(Element link) {
    String href = link.attr("href");
    if (href == null) {
      return false;
    }
    if (link.parent().is("i")) {
      return false;
    }
    if (link.parent().attr("id").equals("coordinates")) {
      return false;
    }
    if (!(link.hasAttr("title") && href.startsWith("/wiki"))) {
      return false;
    }
    for (String pattern : blacklistedUrlPatterns) {
      if (href.contains(pattern)) {
        return false;
      }
    }
    if (isLanguageLink(link)) {
      return false;
    }
    return true;
  }

  private boolean isLanguageLink(Element element) {

    Element parent = element.parent();
    if (!validLinkParents.contains(parent.tag().getName())) {
      element = parent;
    }

    Node previous = element.previousSibling();
    while (previous != null) {
      String text = previous.outerHtml();
      if (text.contains(")")) {
        return false;
      }
      if (text.contains("(")) {
        return true;
      }
      previous = previous.previousSibling();
    }

    Node next = element.nextSibling();
    while (next != null) {
      String text = next.outerHtml();
      if (text.contains("(")) {
        return false;
      }
      if (text.contains(")")) {
        return true;
      }
      next = next.previousSibling();
    }

    return false;
  }
}

