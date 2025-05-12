package com.example.taskservice.config;

import java.io.IOException;

import com.fasterxml.jackson.core.JacksonException;
import com.fasterxml.jackson.core.JsonParser;
import com.fasterxml.jackson.databind.DeserializationContext;
import com.fasterxml.jackson.databind.JavaType;
import com.fasterxml.jackson.databind.JsonDeserializer;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;

public class LenientEnumDeserializer extends JsonDeserializer<Enum<?>> {

    @Override
    public Enum<?> deserialize(JsonParser pJsonParser, DeserializationContext context)
            throws IOException, JacksonException {
        String value = pJsonParser.getText().trim();

        if (value == null || value.isEmpty()) {
            return null;
        }

        // Get the target enum type from the context
        JavaType contextualType = context.getContextualType();

        if (contextualType == null) {
            throw new IllegalStateException("Cannot determine enum type for deserialization");
        }

        // Get the raw class and ensure it's an enum
        Class<?> rawClass = contextualType.getRawClass();

        if (!Enum.class.isAssignableFrom(rawClass)) {
            throw new IllegalStateException("Expected an enum type, but got: " + rawClass.getName());
        }

        // Safe cast to Class<? extends Enum<?>>
        @SuppressWarnings("unchecked")
        Class<? extends Enum<?>> enumType = (Class<? extends Enum<?>>) rawClass;

        try {
            // Use reflection to invoke valueOf on the specific enum class
            Method valueOfMethod = enumType.getMethod("valueOf", String.class);
            return (Enum<?>) valueOfMethod.invoke(null, value.toUpperCase());
        } catch (NoSuchMethodException e) {
            throw new IllegalStateException("Enum class does not have a valueOf method", e);
        } catch (IllegalAccessException | InvocationTargetException e) {
            // Handle invalid enum value or invocation failure
            return null;
        } catch (IllegalArgumentException e) {
            // Handle case where the value doesn't match any enum constant
            return null;
        }
    }
}
