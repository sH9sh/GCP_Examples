package com.example.GCPexamples.example4;

import com.example.GCPexamples.example3.PubSub;
import com.google.cloud.functions.BackgroundFunction;
import com.google.cloud.functions.Context;
import com.google.cloud.storage.BlobId;
import com.google.cloud.storage.BlobInfo;
import com.google.cloud.storage.Storage;
import com.google.cloud.storage.StorageOptions;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;

import java.nio.charset.StandardCharsets;
import java.util.Base64;
import java.util.logging.Logger;

public class PubSubFunction implements BackgroundFunction<PubSubMsg2> {
    private static final Logger logger = Logger.getLogger(PubSub.class.getName());
    private static final String BUCKET_NAME = "cloudBucket";
    private final Storage storage = StorageOptions.getDefaultInstance().getService();

    @Override
    public void accept(PubSubMsg2 message, Context context){
        if(message == null || message.getData() == null){
            logger.severe("Invalid pubSub message: null or missing data");
            return;
        }

        String decodeData = new String(Base64.getDecoder().decode(message.getData()), StandardCharsets.UTF_8);
        logger.info("Decoded pubSub message data: " + decodeData);

        //Parse JSON message
        String filename;
        String fileContent;
        try{
            JsonObject jsonObject = JsonParser.parseString(decodeData).getAsJsonObject();
            filename = jsonObject.get("filename").getAsString();
            fileContent = jsonObject.get("fileContent").getAsString();
        } catch(Exception e){
            logger.severe("Failed to parse pubSub message: " + e.getMessage());
            return;
        }

        // Write to Google Cloud Storage
        BlobId blobId = BlobId.of(BUCKET_NAME, filename);
        BlobInfo blobInfo = BlobInfo.newBuilder(blobId).build();
        storage.create(blobInfo, fileContent.getBytes(StandardCharsets.UTF_8));
        logger.info("File " + filename + " successfully written to bucket " + BUCKET_NAME);
    }
}
