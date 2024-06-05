package com.example.GCPexamples.example5;

import com.example.GCPexamples.example3.PubSub;
import com.example.GCPexamples.example4.PubSubMsg2;
import com.google.cloud.functions.BackgroundFunction;
import com.google.cloud.functions.Context;
import com.google.cloud.storage.BlobId;
import com.google.cloud.storage.BlobInfo;
import com.google.cloud.storage.Storage;
import com.google.cloud.storage.StorageOptions;
import com.google.gson.Gson;
import com.google.cloud.bigquery.*;
import org.springframework.stereotype.Component;

import java.nio.charset.StandardCharsets;
import java.util.HashMap;
import java.util.Map;
import java.util.logging.Logger;

public class PubSubFunction5 implements BackgroundFunction<PubSubMsg5> {

    private static final String BUCKET_NAME = "buckets_of_trouble";
    private final Storage storage = StorageOptions.getDefaultInstance().getService();
    private static final Gson gson = new Gson();
    private static final BigQuery bigQuery = BigQueryOptions.getDefaultInstance().getService();

    @Override
    public void accept(PubSubMsg5 message, Context context) {

        String pubsubMessage = new String(message.getData().getBytes(StandardCharsets.UTF_8));
        Map<String, Object> jsonMap = gson.fromJson(pubsubMessage, Map.class);

        String nodeName = (String) jsonMap.get("nodeName");
        String timeStamp = (String) jsonMap.get("timestamp");
        Double uplink = ((Number) jsonMap.get("uplink")).doubleValue();
        Double downlink = ((Number) jsonMap.get("downlink")).doubleValue();

        writeToGCS(nodeName, timeStamp, pubsubMessage);

        insertToBigQuery(nodeName, timeStamp, uplink, downlink);

    }

    private void writeToGCS(String nodeName, String timestamp, String content){
        String bucketName = "buckets_of_trouble";
        String fileName = String.format("%s-%s.json", nodeName, timestamp);
        BlobId blobId = BlobId.of(bucketName, fileName);
        BlobInfo blobInfo = BlobInfo.newBuilder(blobId).setContentType("application/json").build();
        storage.create(blobInfo, content.getBytes(StandardCharsets.UTF_8));
    }

    private void insertToBigQuery(String nodeName, String timeStamp, Double uplink, Double downlink){
        String datasetName = "big_query_dataset";
        String tableName = "my_table";

        TableId tableId = TableId.of(datasetName, tableName);

        Schema schema = Schema.of(
                Field.of("nodeName", LegacySQLTypeName.STRING),
                Field.of("timeStamp", LegacySQLTypeName.TIMESTAMP),
                Field.of("uplink", LegacySQLTypeName.FLOAT),
                Field.of("downlink", LegacySQLTypeName.FLOAT)
        );

        if (bigQuery.getTable(tableId) == null){
            TableDefinition tableDefinition = StandardTableDefinition.of(schema);
            TableInfo tableInfo = TableInfo.newBuilder(tableId, tableDefinition).build();
            bigQuery.create(tableInfo);
        }

        // insert data into the table
        Map<String, Object> rowContent = new HashMap<>();
        rowContent.put("nodeName", nodeName);
        rowContent.put("timeStamp", timeStamp);
        rowContent.put("uplink", uplink);
        rowContent.put("downlink", downlink);

        InsertAllRequest insertRequest = InsertAllRequest.newBuilder(tableId).addRow(rowContent).build();
        InsertAllResponse insertResponse = bigQuery.insertAll(insertRequest);

        if (insertResponse.hasErrors()){
            insertResponse.getInsertErrors().forEach((index, errors) -> {
                errors.forEach(error -> System.out.println("Error: " + error.getMessage()));
            });
        }
    }
}
