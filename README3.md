Create a topic.

Add a subscription to topic.

Add code to accept a pubSub message. 



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
Add a PubSubMessage DTO class. 



package com.example.GCPexamples.example3;

import java.util.Map;

public class PubSubMessage {
    private String data;

    private Map<String, String> attributes;

    private String messageId;

    private String publishTime;

    public String getData(){
        return data;
    }

    public void setData(String data){
        this.data = data;
    }

    public Map<String, String> getAttributes(){
        return attributes;
    }

    public void setAttributes(Map<String, String> attributes) {
        this.attributes = attributes;
    }

    public String getPublishTime() {
        return publishTime;
    }

    public void setPublishTime(String publishTime) {
        this.publishTime = publishTime;
    }

    public String getMessageId() {
        return messageId;
    }

    public void setMessageId(String messageId) {
        this.messageId = messageId;
    }

}
Run maven clean package to generate new JAR file.

Deploy function with package and class name 



gcloud functions deploy pubSub-function 
--entry-point com.example.GCPexamples.example3.PubSub 
--runtime java17 
--trigger-topic AppTopic 
--allow-unauthenticated 
--gen2 
--source=target
 
Publish message with command below 



gcloud pubsub topics publish my-topic --message "Hello, World!"
View function logs to observe message
