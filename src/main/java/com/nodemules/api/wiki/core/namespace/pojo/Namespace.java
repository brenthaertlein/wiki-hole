package com.nodemules.api.wiki.core.namespace.pojo;

import lombok.Data;

import java.io.Serializable;

@Data
public class Namespace implements Serializable {
    private static final long serialVersionUID = 1756054608977187479L;

    private int id;
    private String name;
}
