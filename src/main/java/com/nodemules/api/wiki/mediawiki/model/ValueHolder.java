package com.nodemules.api.wiki.mediawiki.model;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Data;

import java.io.Serializable;

@Data
public class ValueHolder implements Serializable {
    private static final long serialVersionUID = -7638763339967700376L;

    @JsonProperty("*")
    protected String value;
}
