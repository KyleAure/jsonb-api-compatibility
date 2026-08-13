package com.example;

import static org.junit.jupiter.api.Assertions.assertEquals;

import org.junit.jupiter.api.Test;

import com.example.model.ConstructorPublicField;
import com.example.model.ConstructorPublicSetter;
import com.example.model.FactoryPublicField;
import com.example.model.FactoryPublicSetter;

import jakarta.json.bind.Jsonb;
import jakarta.json.bind.JsonbBuilder;

public class JsonbCreatorTest {

    private Jsonb jsonb = JsonbBuilder.create();

    private static final String testString = "{\"a\": \"TestString\"}";
    private static final String expected = "TESTSTRING";

    @Test
    public void testConstructorPublicField() {
        ConstructorPublicField r = jsonb.fromJson(testString, ConstructorPublicField.class);
        System.out.println("ConstructorPublicField result: " + r.a);
        assertEquals(expected, r.a);
    }

    @Test
    public void testFactoryPublicField() {
        FactoryPublicField r = jsonb.fromJson(testString, FactoryPublicField.class);
        System.out.println("FactoryPublicField result: " + r.a);
        assertEquals(expected, r.a);
    }


    @Test
    public void testConstructorPublicSetter() {
        ConstructorPublicSetter r = jsonb.fromJson(testString, ConstructorPublicSetter.class);
        System.out.println("ConstructorPublicSetter result: " + r.getA());
        assertEquals(expected, r.getA());
    }

    @Test
    public void testFactoryPublicSetter() {
        FactoryPublicSetter r = jsonb.fromJson(testString, FactoryPublicSetter.class);
        System.out.println("FactoryPublicField result: " + r.getA());
        assertEquals(expected, r.getA());
    }
}
