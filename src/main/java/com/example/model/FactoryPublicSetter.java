package com.example.model;

import jakarta.json.bind.annotation.JsonbCreator;
import jakarta.json.bind.annotation.JsonbProperty;

public class FactoryPublicSetter {
    
    private String a;

    @JsonbCreator
    public static FactoryPublicSetter create(@JsonbProperty("a") String a) {
        FactoryPublicSetter inst = new FactoryPublicSetter();
        inst.a = a.toUpperCase();
        return inst;
    }

    public String getA() {
        return a;
    }

    public void setA(String a) {
        this.a = a.toLowerCase();
    }
}
