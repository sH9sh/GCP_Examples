package com.example.GCPexamples.example5.model;

import com.google.cloud.bigquery.Field;
import com.google.cloud.bigquery.Schema;
import com.google.cloud.bigquery.StandardSQLTypeName;
import com.google.cloud.bigquery.storage.v1.TableFieldSchema;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serializable;
import java.time.ZonedDateTime;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class NodeData implements Serializable {
    private static final long serialVersionUID = 1L;
    private String nodeName;
    private ZonedDateTime timeStamp;
    private double uplink;
    private double downlink;

    public static Schema getSchema(){
        return Schema.of(
                Field.newBuilder("nodeName", StandardSQLTypeName.STRING).setMode(Field.Mode.REQUIRED).setDescription("Node Name").build(),
                Field.newBuilder("timeStamp", StandardSQLTypeName.TIMESTAMP).setMode(Field.Mode.REQUIRED).setDescription("Time stamp").build(),
                Field.newBuilder("uplink", StandardSQLTypeName.FLOAT64).setMode(Field.Mode.REQUIRED).setDescription("Uplink value").build(),
                Field.newBuilder("downlink", StandardSQLTypeName.FLOAT64).setMode(Field.Mode.REQUIRED).setDescription("Down link value").build()
        );
    }
}
