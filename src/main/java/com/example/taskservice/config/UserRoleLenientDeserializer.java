package com.example.taskservice.config;

import java.io.IOException;

import com.example.taskservice.model.UserRole;
import com.fasterxml.jackson.core.JacksonException;
import com.fasterxml.jackson.core.JsonParser;
import com.fasterxml.jackson.databind.DeserializationContext;
import com.fasterxml.jackson.databind.JsonDeserializer;

public class UserRoleLenientDeserializer extends JsonDeserializer<UserRole> {

    @Override
    public UserRole deserialize(JsonParser pJsonParser, DeserializationContext context)
            throws IOException, JacksonException {
        String value = pJsonParser.getText().trim();

        if (value == null || value.isEmpty()) {
            return null;
        }

        try {
            return UserRole.valueOf(value.toUpperCase());
        } catch (IllegalArgumentException e) {
            return null; // Lenient behavior: return null for invalid values
        }
    }

}
