package com.example.taskservice.config;

import java.io.IOException;

import com.example.taskservice.model.Priority;
import com.fasterxml.jackson.core.JsonParser;
import com.fasterxml.jackson.databind.DeserializationContext;
import com.fasterxml.jackson.databind.JsonDeserializer;

public class PriorityLenientDeserializer extends JsonDeserializer<Priority> {

    @Override
    public Priority deserialize(JsonParser pJsonParser, DeserializationContext context) throws IOException {
        String value = pJsonParser.getText().trim();

        try {
            return Priority.valueOf(value.toUpperCase());
        } catch (IllegalArgumentException e) {
            return null;
        }
    }

}
