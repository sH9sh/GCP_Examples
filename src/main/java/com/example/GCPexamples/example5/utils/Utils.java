package com.example.GCPexamples.example5.utils;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;

import java.time.Instant;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.time.ZonedDateTime;


public class Utils {

    public static Gson getDefaultGsonInstance(){
        final GsonBuilder gsonBuilder = new GsonBuilder();
        gsonBuilder.registerTypeAdapter(LocalDateTime.class,
                new LocalDateTimeDeserializer("yyyy-MM-dd'T'HH:mm:ss", ZoneId.of("UTC")));
        gsonBuilder.registerTypeAdapter(LocalDateTime.class,
                new LocalDateTimeSerializer("yyyy-MM-dd'T'HH:mm:ss", ZoneId.of("UTC")));
        gsonBuilder.registerTypeAdapter(ZonedDateTime.class,
                new ZonedDateTimeHandler("yyyy-MM-dd'T'HH:mm:ss'Z'", ZoneId.of("UTC")));
        gsonBuilder.registerTypeAdapter(Instant.class,
                new InstantHandler("yyyy-MM-dd'T'HH:mm:ss'Z'", ZoneId.of("UTC")));
        return gsonBuilder.setPrettyPrinting().create();

    }
}
