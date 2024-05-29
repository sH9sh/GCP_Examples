package com.example.GCPexamples.example2;

import com.google.cloud.functions.HttpFunction;
import com.google.cloud.functions.HttpRequest;
import com.google.cloud.functions.HttpResponse;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.logging.Logger;

public class ScheduledFunction implements HttpFunction {

    private static final Logger logger = Logger.getLogger(ScheduledFunction.class.getName());
    @Override
    public void service(HttpRequest request, HttpResponse response){

        LocalDateTime currentTime = LocalDateTime.now();
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");
        String formattedTime = currentTime.format(formatter);

        logger.info("Current Time: " + formattedTime);
    }
}
