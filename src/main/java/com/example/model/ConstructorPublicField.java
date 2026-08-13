package com.example.model;

import jakarta.json.bind.annotation.JsonbCreator;
import jakarta.json.bind.annotation.JsonbProperty;

public class ConstructorPublicField {

    public String a;

    @JsonbCreator
    public ConstructorPublicField(@JsonbProperty("a") String a) {
        this.a = a.toUpperCase();
    }
    
}
