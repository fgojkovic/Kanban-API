package com.example.taskservice.config;

import java.io.IOException;

import com.example.taskservice.model.Priority;
import com.fasterxml.jackson.core.JacksonException;
import com.fasterxml.jackson.core.JsonParser;
import com.fasterxml.jackson.databind.DeserializationContext;
import com.fasterxml.jackson.databind.JsonDeserializer;

public class PriorityLenientDeserializer extends JsonDeserializer<Priority> {

    @Override
    public Priority deserialize(JsonParser pJsonParser, DeserializationContext context)
            throws IOException, JacksonException {
        String value = pJsonParser.getText().trim();

        if (value == null || value.isEmpty()) {
            return null;
        }

        try {
            return Priority.valueOf(value.toUpperCase());
        } catch (IllegalArgumentException e) {
            return null; // Lenient behavior: return null for invalid values
        }
    }

}
