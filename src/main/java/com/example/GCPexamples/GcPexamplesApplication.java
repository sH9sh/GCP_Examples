package com.example.GCPexamples;

import com.example.GCPexamples.example5.handlers.BigQueryHandler;
import com.example.GCPexamples.example5.handlers.StorageHandler;
import com.example.GCPexamples.example5.model.NodeData;
import com.example.GCPexamples.example5.model.PubSubMsg5;
import com.example.GCPexamples.example5.utils.Utils;
import com.google.gson.Gson;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.context.properties.ConfigurationPropertiesScan;
import org.springframework.context.annotation.Bean;

import java.nio.charset.StandardCharsets;
import java.util.Base64;
import java.util.function.Consumer;
import java.util.logging.Level;
import java.util.logging.Logger;

@SpringBootApplication
@ConfigurationPropertiesScan
public class GcPexamplesApplication {

	private static Logger logger = Logger.getLogger(GcPexamplesApplication.class.getName());
	private final Gson gson = Utils.getDefaultGsonInstance();
	private static final String BUCKET_NAME = "question5-bucket";
	private static final String TABLE_NAME_KEY = "TABLE_NAME";

	@Autowired
	private StorageHandler storageHandler;
	@Autowired
	private BigQueryHandler bigQueryHandler;

	public static void main(String[] args) {
		SpringApplication.run(GcPexamplesApplication.class, args);
	}

	@Bean
	public Consumer<PubSubMsg5> pubSubFunction(){
		return message -> {
			try{
				final String encodedData = message.getData();
				final String decodedData = new String(Base64.getDecoder().decode(encodedData), StandardCharsets.UTF_8);
				logger.info("Message received - " + decodedData);

				final NodeData nodeData = gson.fromJson(decodedData, NodeData.class);

				// create bucket
				storageHandler.createBucket(BUCKET_NAME);

				// store file
				final String path = storageHandler.uploadFile(BUCKET_NAME, this.getFileName(nodeData), decodedData);
				logger.info("File created at - " + path);

				final String tableName = this.getTableName();
				logger.info("Table name - " + tableName);

				// create bq table
				bigQueryHandler.createTable(tableName, NodeData.getSchema());

				bigQueryHandler.writeNodeDataToBq(decodedData, tableName);
			}catch (final Exception e){
				logger.log(Level.SEVERE, "Failed to process message. Reason - " + e);
			}
		};
	}

	private String getFileName(final NodeData nodeData){
		return nodeData.getNodeName() + "/" + nodeData.getTimeStamp().toString() + ".json";
	}

	private String getTableName(){
		if(System.getenv().containsKey(TABLE_NAME_KEY)){
			return System.getenv(TABLE_NAME_KEY);
		}

		if(System.getenv().containsKey(TABLE_NAME_KEY)){
			return System.getProperty(TABLE_NAME_KEY);
		}
		return null;

	}

}
