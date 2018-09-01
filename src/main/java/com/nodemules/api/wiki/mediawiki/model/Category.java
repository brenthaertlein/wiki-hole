package com.nodemules.api.wiki.mediawiki.model;

import com.fasterxml.jackson.databind.PropertyNamingStrategy;
import com.fasterxml.jackson.databind.annotation.JsonNaming;
import lombok.Data;

@Data
@JsonNaming(PropertyNamingStrategy.LowerCaseStrategy.class)
public class Category extends ValueHolder {
    private static final long serialVersionUID = -8940944705838824084L;

    private String sortKey;
    private String hidden;
}
