package com.example.GCPexamples.example4;

import com.example.GCPexamples.example3.PubSub;
import com.google.cloud.functions.BackgroundFunction;
import com.google.cloud.functions.Context;
import com.google.cloud.storage.BlobId;
import com.google.cloud.storage.BlobInfo;
import com.google.cloud.storage.Storage;
import com.google.cloud.storage.StorageOptions;
import com.google.gson.Gson;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;

import java.nio.charset.StandardCharsets;
import java.util.Base64;
import java.util.logging.Logger;

public class PubSubFunction implements BackgroundFunction<PubSubMsg2> {
    private static final Logger logger = Logger.getLogger(PubSub.class.getName());
    private static final String BUCKET_NAME = "buckets_of_trouble";
    private final Storage storage = StorageOptions.getDefaultInstance().getService();
    private static final Gson gson = new Gson();

    @Override
    public void accept(PubSubMsg2 message, Context context){
        if(message == null || message.getData() == null){
            logger.severe("Invalid pubSub message: null or missing data");
            return;
        }

        String decodeData = new String(Base64.getDecoder().decode(message.getData()), StandardCharsets.UTF_8);
        JsonObject jsonObject = gson.fromJson(decodeData, JsonObject.class);


        //Parse JSON message
        String filename = jsonObject.get("filename").getAsString();
        String fileContent = jsonObject.get("fileContent").getAsString();


        // Write to Google Cloud Storage
        BlobId blobId = BlobId.of(BUCKET_NAME, filename);
        BlobInfo blobInfo = BlobInfo.newBuilder(blobId).build();
        storage.create(blobInfo, fileContent.getBytes(StandardCharsets.UTF_8));
        logger.info("File " + filename + " successfully written to bucket " + BUCKET_NAME);
    }
}
