package com.example.taskservice.config;

import java.io.IOException;

import com.example.taskservice.model.UserRole;
import com.fasterxml.jackson.core.JsonParser;
import com.fasterxml.jackson.databind.DeserializationContext;
import com.fasterxml.jackson.databind.JsonDeserializer;

public class UserRoleLenientDeserializer extends JsonDeserializer<UserRole> {

    @Override
    public UserRole deserialize(JsonParser pJsonParser, DeserializationContext context) throws IOException {
        String value = pJsonParser.getText().trim();

        try {
            return UserRole.valueOf(value.toUpperCase());
        } catch (IllegalArgumentException e) {
            return null;
        }
    }

}
