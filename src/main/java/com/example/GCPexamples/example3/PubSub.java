package com.example.GCPexamples.example3;

import com.google.cloud.functions.BackgroundFunction;
import com.google.cloud.functions.Context;

import java.nio.charset.StandardCharsets;
import java.util.Base64;
import java.util.logging.Logger;

public class PubSub implements BackgroundFunction<PubSubMessage> {

    private static final Logger logger = Logger.getLogger(PubSub.class.getName());

    @Override
    public void accept(PubSubMessage message, Context context){
        String name = "world!";
        if(message != null && message.getData() != null){
            name = new String(
                    Base64.getDecoder().decode(message.getData().getBytes(StandardCharsets.UTF_8)),
                    StandardCharsets.UTF_8);
        }
        logger.info(String.format("Hello %s!", name));
        return;
    }
}
