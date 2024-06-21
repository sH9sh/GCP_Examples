package com.example.GCPexamples.example5.utils;

import com.google.gson.TypeAdapter;
import com.google.gson.stream.JsonReader;
import com.google.gson.stream.JsonWriter;

import java.io.IOException;
import java.time.ZoneId;
import java.time.ZonedDateTime;
import java.time.format.DateTimeFormatter;
import java.time.temporal.ChronoUnit;


// define your own date time formatter
public class ZonedDateTimeHandler extends TypeAdapter<ZonedDateTime> {
    public static final String TIME_UTC = "yyyy-MM-dd'T'HH:mm:ssz";
    public static final String ZONE_UTC = "UTC";
    private DateTimeFormatter dt_formatter;
    private String pattern = TIME_UTC;
    private ZoneId zoneId = ZoneId.of(ZONE_UTC);

    public ZonedDateTimeHandler(){
        this.dt_formatter =
                DateTimeFormatter.ofPattern(this.pattern).withZone(this.zoneId);
    }

    public ZonedDateTimeHandler(final String pattern, final ZoneId zoneId){
        this.pattern = pattern;
        this.zoneId = zoneId;
        this.dt_formatter =
                DateTimeFormatter.ofPattern(this.pattern).withZone(this.zoneId);
    }

    @Override
    public void write(JsonWriter out, ZonedDateTime value) throws IOException{
        if(value != null){
            out.value(value.truncatedTo(ChronoUnit.SECONDS).format(this.dt_formatter).toString());
        } else{
            out.nullValue();
        }
    }

    @Override
    public ZonedDateTime read(JsonReader in) throws IOException{
        return ZonedDateTime.parse(in.nextString())
                .truncatedTo(ChronoUnit.SECONDS);
    }
}
