package com.example.GCPexamples.example1;

import com.google.cloud.functions.HttpFunction;
import com.google.cloud.functions.HttpRequest;
import com.google.cloud.functions.HttpResponse;
import com.example.GCPexamples.example2.ScheduledFunction;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.logging.Logger;

public class HttpMethod implements HttpFunction {
    private static final Logger logger = Logger.getLogger(ScheduledFunction.class.getName());
    @Override
    public void service(HttpRequest request, HttpResponse response) throws Exception {

//        BufferedWriter writer = response.getWriter();
//
//        switch(request.getMethod()){
//            case "GET":
//                response.setStatusCode(HttpURLConnection.HTTP_OK);
//                writer.write("Get request was successful");
//                break;
//            case "POST":
//                response.setStatusCode(HttpURLConnection.HTTP_OK);
//                writer.write("POST request was successful");
//
//                break;
//            case "DELETE":
//                response.setStatusCode(HttpURLConnection.HTTP_OK);
//                writer.write("DELETE request worked successfully");
//                break;
//            default:
//                response.setStatusCode(HttpURLConnection.HTTP_BAD_METHOD);
//                writer.write("This is not working");
//                break;
//        }

        LocalDateTime currentTime = LocalDateTime.now();
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");
        String formattedTime = currentTime.format(formatter);

        logger.info("Current Time: " + formattedTime);
    }
}
