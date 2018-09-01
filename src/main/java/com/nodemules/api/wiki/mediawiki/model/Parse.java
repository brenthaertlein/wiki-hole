package com.nodemules.api.wiki.mediawiki.model;

import com.fasterxml.jackson.databind.PropertyNamingStrategy;
import com.fasterxml.jackson.databind.annotation.JsonNaming;
import lombok.Data;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;
import lombok.EqualsAndHashCode;

@Data
@EqualsAndHashCode(callSuper = true)
@JsonNaming(PropertyNamingStrategy.LowerCaseStrategy.class)
public class Parse extends Page implements Serializable {
    private static final long serialVersionUID = 6120064789879513131L;

    private int revId;
    private Text text;

    private List<Category> categories = new ArrayList<>();
    private List<Link> links = new ArrayList<>();

    private WikiText wikiText;

    private List<Redirect> redirects = new ArrayList<>();
}
