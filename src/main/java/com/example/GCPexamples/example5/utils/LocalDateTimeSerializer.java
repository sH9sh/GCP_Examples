package com.example.GCPexamples.example5.utils;

import com.google.gson.JsonElement;
import com.google.gson.JsonPrimitive;
import com.google.gson.JsonSerializationContext;
import com.google.gson.JsonSerializer;

import java.lang.reflect.Type;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.time.format.DateTimeFormatter;
import java.util.Locale;


// convert Local Date Time object to JSON
public class LocalDateTimeSerializer implements JsonSerializer <LocalDateTime> {
    private String pattern;
    private ZoneId zone;
    private DateTimeFormatter formatter;

    public LocalDateTimeSerializer(final String pattern, final ZoneId zone){
        super();
        this.pattern = pattern;
        this.zone = zone;
        this.formatter = DateTimeFormatter.ofPattern(this.pattern)
                .withZone(this.zone)
                .withLocale(Locale.ENGLISH);
    }

    @Override
    public JsonElement serialize(LocalDateTime localDateTime, Type srcType, JsonSerializationContext context){
        return new JsonPrimitive(formatter.format(localDateTime));
    }

}
