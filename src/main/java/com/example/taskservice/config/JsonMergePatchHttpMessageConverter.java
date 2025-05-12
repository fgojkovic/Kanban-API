package com.example.taskservice.config;

import org.springframework.http.HttpInputMessage;
import org.springframework.http.HttpOutputMessage;
import org.springframework.http.MediaType;
import org.springframework.http.converter.AbstractHttpMessageConverter;
import org.springframework.http.converter.HttpMessageNotReadableException;
import org.springframework.http.converter.HttpMessageNotWritableException;
import org.springframework.lang.NonNull;
import org.springframework.stereotype.Component;

import javax.json.Json;
import javax.json.JsonMergePatch;
import javax.json.JsonReader;
import javax.json.JsonValue;
import java.io.IOException;
import java.io.OutputStreamWriter;
import java.nio.charset.StandardCharsets;

@Component
public class JsonMergePatchHttpMessageConverter extends AbstractHttpMessageConverter<JsonMergePatch> {

    public JsonMergePatchHttpMessageConverter() {
        super(new MediaType("application", "merge-patch+json"));
    }

    @Override
    protected boolean supports(@NonNull Class<?> clazz) {
        return JsonMergePatch.class.isAssignableFrom(clazz);
    }

    @Override
    @NonNull
    protected JsonMergePatch readInternal(@NonNull Class<? extends JsonMergePatch> clazz,
            @NonNull HttpInputMessage inputMessage)
            throws IOException, HttpMessageNotReadableException {
        try (JsonReader reader = Json.createReader(inputMessage.getBody())) {
            JsonValue jsonValue = reader.readValue();
            return Json.createMergePatch(jsonValue);
        } catch (Exception e) {
            throw new HttpMessageNotReadableException("Error reading JSON Merge Patch", e, inputMessage);
        }
    }

    // Probbably not needed, but included for completeness
    @Override
    protected void writeInternal(@NonNull JsonMergePatch mergePatch, @NonNull HttpOutputMessage outputMessage)
            throws IOException, HttpMessageNotWritableException {
        try (OutputStreamWriter writer = new OutputStreamWriter(outputMessage.getBody(), StandardCharsets.UTF_8)) {
            JsonValue jsonValue = mergePatch.toJsonValue();
            writer.write(jsonValue.toString());
            writer.flush();
        } catch (Exception e) {
            throw new HttpMessageNotWritableException("Error writing JSON Merge Patch", e);
        }
    }
}