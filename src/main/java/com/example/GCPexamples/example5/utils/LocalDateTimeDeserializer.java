package com.example.GCPexamples.example5.utils;

import com.google.gson.JsonDeserializationContext;
import com.google.gson.JsonDeserializer;
import com.google.gson.JsonElement;
import com.google.gson.JsonParseException;

import java.lang.reflect.Type;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.time.format.DateTimeFormatter;
import java.util.Locale;


// Convert from JSON to Local Date time object
public class LocalDateTimeDeserializer implements JsonDeserializer<LocalDateTime> {
    private String pattern;
    private ZoneId zone;

    public LocalDateTimeDeserializer(final String pattern, final ZoneId zone){
        super();
        this.pattern = pattern;
        this.zone = zone;
    }

    @Override
    public LocalDateTime deserialize(
            JsonElement json, Type typeOfT, JsonDeserializationContext context)
        throws JsonParseException {
        return LocalDateTime.parse(
                json.getAsString(),
                DateTimeFormatter.ofPattern(this.pattern).withZone(this.zone).withLocale(Locale.ENGLISH));
    }
}
