package com.example.model;

import jakarta.json.bind.annotation.JsonbCreator;
import jakarta.json.bind.annotation.JsonbProperty;

public class ConstructorPublicSetter {
    
    private String a;

    @JsonbCreator
    public ConstructorPublicSetter(@JsonbProperty("a") String a) {
        this.a = a.toUpperCase();
    }

    public String getA() {
        return a;
    }

    public void setA(String a) {
        this.a = a.toLowerCase();
    }
}
