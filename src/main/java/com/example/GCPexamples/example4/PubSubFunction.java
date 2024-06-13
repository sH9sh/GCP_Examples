package com.example.GCPexamples.example4;

import com.google.cloud.functions.CloudEventsFunction;
import com.google.gson.Gson;
import io.cloudevents.CloudEvent;

import java.nio.charset.StandardCharsets;
import java.util.Base64;
import java.util.logging.Logger;

public class PubSubFunction implements CloudEventsFunction {
    private static final Logger logger = Logger.getLogger(PubSubFunction.class.getName());
    private static final String BUCKET_NAME = "question4-bucket";
    private final StorageHandler storageHandler = new StorageHandler();
    private static final Gson gson = new Gson();

    @Override
    public void accept(final CloudEvent event){
        if(event.getData() != null){
            final String cloudEventData = new String(event.getData().toBytes(), StandardCharsets.UTF_8);
            final PubSubBody body = gson.fromJson(cloudEventData, PubSubBody.class);

            final String encodedData = body.getMessage().getData();
            final String decodedData = new String(Base64.getDecoder().decode(encodedData), StandardCharsets.UTF_8);
            logger.info("Message received - " + decodedData);

            final FileDetails fileDetails = gson.fromJson(decodedData, FileDetails.class);
            logger.info("File name - " + fileDetails.getFileName());

            // create bucket
            storageHandler.createBucket(BUCKET_NAME);

            // store file
            final String path = storageHandler.uploadFile(BUCKET_NAME, fileDetails.getFileName(), fileDetails.getFileContent());
            logger.info("File created at - " + path);

        }
    }
}
