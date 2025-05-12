package com.example.taskservice.config;

import java.io.IOException;

import com.example.taskservice.model.Status;
import com.fasterxml.jackson.core.JacksonException;
import com.fasterxml.jackson.core.JsonParser;
import com.fasterxml.jackson.databind.DeserializationContext;
import com.fasterxml.jackson.databind.JsonDeserializer;

public class StatusLenientDeserializer extends JsonDeserializer<Status> {

    @Override
    public Status deserialize(JsonParser pJsonParser, DeserializationContext context)
            throws IOException, JacksonException {
        String value = pJsonParser.getText().trim();

        if (value == null || value.isEmpty()) {
            return null;
        }

        try {
            return Status.valueOf(value.toUpperCase());
        } catch (IllegalArgumentException e) {
            return null; // Lenient behavior: return null for invalid values
        }
    }

}
