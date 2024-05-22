package com.example.GCPexamples;

import com.google.cloud.functions.HttpFunction;
import com.google.cloud.functions.HttpRequest;
import com.google.cloud.functions.HttpResponse;

import java.io.BufferedWriter;
import java.net.HttpURLConnection;

public class HttpMethod implements HttpFunction {
    @Override
    public void service(HttpRequest request, HttpResponse response) throws Exception {

        BufferedWriter writer = response.getWriter();

        switch(request.getMethod()){
            case "GET":
                response.setStatusCode(HttpURLConnection.HTTP_OK);
                writer.write("Get request was successful");
                break;
            case "PUT":
                response.setStatusCode(HttpURLConnection.HTTP_FORBIDDEN);
                writer.write("Forbidden");
                break;
            default:
                response.setStatusCode(HttpURLConnection.HTTP_BAD_METHOD);
                writer.write("This is not working");
                break;
        }
    }
}
