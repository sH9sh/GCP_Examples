package com.example.GCPexamples.example5.utils;

import com.google.gson.TypeAdapter;
import com.google.gson.stream.JsonReader;
import com.google.gson.stream.JsonToken;
import com.google.gson.stream.JsonWriter;

import java.io.IOException;
import java.time.Instant;
import java.time.ZoneId;
import java.time.format.DateTimeFormatter;
import java.time.format.DateTimeParseException;
import java.time.temporal.ChronoUnit;
import java.time.temporal.TemporalAccessor;

public class InstantHandler extends TypeAdapter<Instant> {
    private DateTimeFormatter dt_formatter;
    private String pattern;
    private ZoneId zoneId;

    public InstantHandler(final String pattern, final ZoneId zoneId){
        this.pattern = pattern;
        this.zoneId = zoneId;
        this.dt_formatter = DateTimeFormatter.ofPattern(this.pattern).withZone(this.zoneId);
    }

    @Override
    public void write(JsonWriter out, Instant value) throws IOException {
        if(value == null) {
            out.nullValue();
        } else {
            out.value(dt_formatter.format(value.truncatedTo(ChronoUnit.SECONDS)));
        }

    }

    @Override
    public Instant read(JsonReader in) throws IOException {
        JsonToken token = in.peek();
        if (token == JsonToken.NULL) {
            in.nextNull();
            return null;
        }
        String instantStr = in.nextString();
        return parse(instantStr);
    }

    protected Instant parse(String instantStr) {
        if (instantStr == null) {
            return null;
        }
        TemporalAccessor accessor = null;
        try {
            accessor = dt_formatter.parse(instantStr);
            return Instant.from(accessor);
        } catch (DateTimeParseException ignore) {
        }

        if (accessor == null) {
            try {
                final String[] temp = instantStr.split("[.]");
                final String epochSeconds = temp[0];
                accessor = dt_formatter.parse(Instant.ofEpochSecond(Long.parseLong(epochSeconds)).toString());
                return Instant.from(accessor);
            } catch (final Exception e) {
            }
        }
        return null;
    }
}
