package com.example.GCPexamples.example5.handlers;

import java.util.logging.Logger;

import org.springframework.stereotype.Service;

import com.google.cloud.storage.Blob;
import com.google.cloud.storage.Bucket;
import com.google.cloud.storage.BucketInfo;
import com.google.cloud.storage.Storage;
import com.google.cloud.storage.StorageOptions;
import com.google.common.net.MediaType;

@Service
public class StorageHandler {
    private static final Logger logger = Logger.getLogger(StorageHandler.class.getName());
    private Storage storage = StorageOptions.getDefaultInstance().getService();

    public boolean ifBucketExists(final String bucketName) {
        logger.info("Checking if bucket " + bucketName + " exists");
        boolean result = false;
        final Bucket bucket = storage.get(bucketName);
        if(bucket != null) {
            result = true;
        }

        logger.info("Bucket " + bucketName + " exist - " + result);
        return result;
    }

    public Bucket getBucket(final String bucketName) {
        return storage.get(bucketName);
    }

    public void createBucket(final String bucketName) {
        if(!this.ifBucketExists(bucketName)) {
            logger.info("Creating bucket - " + bucketName);
            this.storage.create(BucketInfo.of(bucketName));
        }
    }

    public String uploadFile(final String bucketName, final String fileName, final String fileContent) {
        final Bucket bucket = this.getBucket(bucketName);
        logger.info("Creating object in bucket...");
        final Blob blob = bucket.create(fileName, fileContent.getBytes(), MediaType.JSON_UTF_8.toString());
        logger.info("Blob created at - " + blob.getSelfLink());
        return blob.getSelfLink();
    }

}
