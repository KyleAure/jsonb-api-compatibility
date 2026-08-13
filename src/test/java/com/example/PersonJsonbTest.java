package com.example;

import com.example.model.Person;
import jakarta.json.bind.Jsonb;
import jakarta.json.bind.JsonbBuilder;
import jakarta.json.bind.JsonbConfig;
import jakarta.json.bind.config.PropertyNamingStrategy;
import org.junit.jupiter.api.Test;

import java.time.LocalDate;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

class PersonJsonbTest {

    private Jsonb jsonb = JsonbBuilder.create();

    // -------------------------------------------------------------------------
    // Serialisation (toJson)
    // -------------------------------------------------------------------------

    @Test
    void serialize_basicFields() {
        Person person = new Person("Jane", "Doe", LocalDate.of(1990, 6, 15), List.of("reading", "cycling"));

        String json = jsonb.toJson(person);

        assertTrue(json.contains("\"firstName\":\"Jane\""), "firstName should be serialised");
        assertTrue(json.contains("\"lastName\":\"Doe\""),   "lastName should be serialised");
    }

    @Test
    void serialize_jsonbPropertyRenamesField() {
        Person person = new Person("Jane", "Doe", LocalDate.of(1990, 6, 15), null);

        String json = jsonb.toJson(person);

        // @JsonbProperty("dob") on dateOfBirth — key must be "dob", not "dateOfBirth"
        assertTrue(json.contains("\"dob\""),          "field should be renamed to 'dob'");
        assertFalse(json.contains("\"dateOfBirth\""), "'dateOfBirth' must not appear");
    }

    @Test
    void serialize_jsonbTransientExcludesField() {
        Person person = new Person("Jane", "Doe", LocalDate.of(1990, 6, 15), null);
        person.setInternalNote("secret");

        String json = jsonb.toJson(person);

        assertFalse(json.contains("internalNote"), "@JsonbTransient field must not appear in JSON");
        assertFalse(json.contains("secret"),       "value of @JsonbTransient field must not appear");
    }

    @Test
    void serialize_listField() {
        Person person = new Person("Jane", "Doe", LocalDate.of(1990, 6, 15), List.of("reading", "cycling"));

        String json = jsonb.toJson(person);

        assertTrue(json.contains("\"reading\""),  "list element 'reading' should be present");
        assertTrue(json.contains("\"cycling\""),  "list element 'cycling' should be present");
    }

    @Test
    void serialize_nullListFieldOmittedOrNull() {
        Person person = new Person("Jane", "Doe", LocalDate.of(1990, 6, 15), null);

        // Null collections are serialised as JSON null by default — just assert no exception
        assertDoesNotThrow(() -> jsonb.toJson(person));
    }

    // -------------------------------------------------------------------------
    // Deserialisation (fromJson)
    // -------------------------------------------------------------------------

    @Test
    void deserialize_basicFields() {
        String json = "{\"firstName\":\"John\",\"lastName\":\"Smith\",\"dob\":\"1985-03-22\"}";

        Person person = jsonb.fromJson(json, Person.class);

        assertEquals("John",  person.getFirstName());
        assertEquals("Smith", person.getLastName());
        assertEquals(LocalDate.of(1985, 3, 22), person.getDateOfBirth());
    }

    @Test
    void deserialize_jsonbPropertyMapsRenamedKey() {
        String json = "{\"firstName\":\"John\",\"lastName\":\"Smith\",\"dob\":\"2000-01-01\"}";

        Person person = jsonb.fromJson(json, Person.class);

        assertEquals(LocalDate.of(2000, 1, 1), person.getDateOfBirth(),
                "Key 'dob' should map back to dateOfBirth via @JsonbProperty");
    }

    @Test
    void deserialize_listField() {
        String json = "{\"firstName\":\"A\",\"lastName\":\"B\",\"hobbies\":[\"hiking\",\"cooking\"]}";

        Person person = jsonb.fromJson(json, Person.class);

        assertNotNull(person.getHobbies());
        assertEquals(List.of("hiking", "cooking"), person.getHobbies());
    }

    @Test
    void deserialize_unknownFieldsIgnored() {
        // Extra "unknownField" should not cause an exception
        String json = "{\"firstName\":\"A\",\"lastName\":\"B\",\"unknownField\":\"ignored\"}";

        assertDoesNotThrow(() -> jsonb.fromJson(json, Person.class));
    }

    @Test
    void deserialize_jsonbTransientFieldNotPopulated() {
        String json = "{\"firstName\":\"A\",\"lastName\":\"B\",\"internalNote\":\"injected\"}";

        Person person = jsonb.fromJson(json, Person.class);

        // @JsonbTransient — the field must remain null even if the key is present in JSON
        assertNull(person.getInternalNote(), "@JsonbTransient field must not be populated on deserialise");
    }

    // -------------------------------------------------------------------------
    // Round-trip
    // -------------------------------------------------------------------------

    @Test
    void roundTrip_personIsRestoredFaithfully() {
        Person original = new Person("Alice", "Wonder", LocalDate.of(1995, 12, 31), List.of("chess", "swimming"));
        original.setInternalNote("should not survive round-trip");

        String json   = jsonb.toJson(original);
        Person restored = jsonb.fromJson(json, Person.class);

        assertEquals(original.getFirstName(),   restored.getFirstName());
        assertEquals(original.getLastName(),    restored.getLastName());
        assertEquals(original.getDateOfBirth(), restored.getDateOfBirth());
        assertEquals(original.getHobbies(),     restored.getHobbies());
        assertNull(restored.getInternalNote(),  "@JsonbTransient must not survive round-trip");
    }

    // -------------------------------------------------------------------------
    // JsonbConfig — custom naming strategy
    // -------------------------------------------------------------------------

    @Test
    void config_upperCamelCaseNamingStrategy() throws Exception {
        JsonbConfig config = new JsonbConfig()
                .withPropertyNamingStrategy(PropertyNamingStrategy.UPPER_CAMEL_CASE);

        try (Jsonb customJsonb = JsonbBuilder.create(config)) {
            Person person = new Person("Bob", "Builder", LocalDate.of(1975, 7, 4), null);
            String json = customJsonb.toJson(person);

            assertTrue(json.contains("\"FirstName\""), "UPPER_CAMEL_CASE should capitalise 'firstName' → 'FirstName'");
            assertTrue(json.contains("\"LastName\""),  "UPPER_CAMEL_CASE should capitalise 'lastName' → 'LastName'");
        }
    }
}
