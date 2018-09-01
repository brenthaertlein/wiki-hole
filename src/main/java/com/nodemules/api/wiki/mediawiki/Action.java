package com.nodemules.api.wiki.mediawiki;

import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public enum Action {

    PARSE("parse"),
    QUERY("query");

    private final String value;
}
