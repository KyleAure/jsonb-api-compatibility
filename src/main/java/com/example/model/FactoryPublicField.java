package com.example.model;

import jakarta.json.bind.annotation.JsonbCreator;
import jakarta.json.bind.annotation.JsonbProperty;

public class FactoryPublicField {
    
    public String a;

    @JsonbCreator
    public static FactoryPublicField create(@JsonbProperty("a") String a) {
        FactoryPublicField inst = new FactoryPublicField();
        inst.a = a.toUpperCase();
        return inst;
    }

}
