Google Cloud Functions Examples
This repository contains examples of deploying various types of Google Cloud Functions using Java. The examples include HTTP functions, scheduled functions, Pub/Sub functions, and storage-triggered functions.

Table of Contents
Deploying an HTTP Function
Deploying a Scheduled Function
Deploying a Pub/Sub Function
Triggering a Function with Cloud Storage
Prerequisites
Java 17
Google Cloud SDK
Maven
A Google Cloud project
Deploying an HTTP Function
You can define a custom build process by adding a cloudbuild.yaml file. However, you can also deploy via gcloud command line without this.

Using cloudbuild.yaml
Create a cloudbuild.yaml file:

yaml
Copy code
steps:
  - name: 'gcr.io/google.com/cloudsdktool/cloud-sdk'
    args:
      - gcloud
      - functions
      - deploy
      - function-gcp
      - --region=us-central1
      - --source=.
      - --trigger-http
      - --runtime=java17
      - --entry-point=com.example.GCPexamples.example5.PubSubFunction5
Trigger the build:

bash
Copy code
gcloud builds submit --config cloudbuild.yaml .
Adding Code for the Function
The class will implement the HttpFunction interface. It will use the service method which has two parameters - HttpRequest and HttpResponse.

java
Copy code
package com.example.GCPexamples.example1;

import com.google.cloud.functions.HttpFunction;
import com.google.cloud.functions.HttpRequest;
import com.google.cloud.functions.HttpResponse;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.logging.Logger;

public class HttpMethod implements HttpFunction {
    private static final Logger logger = Logger.getLogger(HttpMethod.class.getName());

    @Override
    public void service(HttpRequest request, HttpResponse response) throws Exception {
        LocalDateTime currentTime = LocalDateTime.now();
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");
        String formattedTime = currentTime.format(formatter);
        logger.info("Current Time: " + formattedTime);
    }
}
Ensure to add necessary dependencies like cloud functions. Also make sure to reference the function to be deployed under 'function-maven-plugin' inside functionTarget tags.

Deploy Function via gcloud Command Line
bash
Copy code
gcloud functions deploy HttpMethod \
    --entry-point=com.example.GCPexamples.example1.HttpMethod \
    --runtime=java17 \
    --trigger-http \
    --memory=512MB \
    --region=us-central1
Deploying a Scheduled Function
Add code for the scheduled function. This also implements the HttpFunction interface and uses the service method. Add code to log the current time when the function is triggered.

java
Copy code
package com.example.GCPexamples.example2;

import com.google.cloud.functions.HttpFunction;
import com.google.cloud.functions.HttpRequest;
import com.google.cloud.functions.HttpResponse;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.logging.Logger;

public class ScheduledFunction implements HttpFunction {
    private static final Logger logger = Logger.getLogger(ScheduledFunction.class.getName());

    @Override
    public void service(HttpRequest request, HttpResponse response) {
        LocalDateTime currentTime = LocalDateTime.now();
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");
        String formattedTime = currentTime.format(formatter);
        logger.info("Current Time: " + formattedTime);
    }
}
Deploy function as done in question 1:

bash
Copy code
gcloud functions deploy ScheduledFunction \
    --entry-point=com.example.GCPexamples.example2.ScheduledFunction \
    --runtime=java17 \
    --trigger-http \
    --memory=512MB \
    --region=us-central1
Use Cloud Scheduler on GCP to trigger the function on a set time. Set target type as HTTP, HTTP method as OPTIONS. Add the URL of the deployed function. Hit create scheduler. The function will be triggered periodically as configured.

Deploying a Pub/Sub Function
Create a topic and add a subscription to the topic.

Pub/Sub Function Code
java
Copy code
package com.example.GCPexamples.example3;

import com.google.cloud.functions.BackgroundFunction;
import com.google.cloud.functions.Context;
import java.nio.charset.StandardCharsets;
import java.util.Base64;
import java.util.logging.Logger;

public class PubSub implements BackgroundFunction<PubSubMessage> {
    private static final Logger logger = Logger.getLogger(PubSub.class.getName());

    @Override
    public void accept(PubSubMessage message, Context context) {
        String name = "world!";
        if (message != null && message.getData() != null) {
            name = new String(
                Base64.getDecoder().decode(message.getData().getBytes(StandardCharsets.UTF_8)),
                StandardCharsets.UTF_8
            );
        }
        logger.info(String.format("Hello %s!", name));
    }
}
PubSubMessage DTO
java
Copy code
package com.example.GCPexamples.example3;

import java.util.Map;

public class PubSubMessage {
    private String data;
    private Map<String, String> attributes;
    private String messageId;
    private String publishTime;

    public String getData() {
        return data;
    }

    public void setData(String data) {
        this.data = data;
    }

    public Map<String, String> getAttributes() {
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
Run maven clean package to generate a new JAR file.

Deploy Function
bash
Copy code
gcloud functions deploy pubSub-function \
    --entry-point=com.example.GCPexamples.example3.PubSub \
    --runtime=java17 \
    --trigger-topic=AppTopic \
    --allow-unauthenticated \
    --gen2 \
    --source=target
Publish a message:

bash
Copy code
gcloud pubsub topics publish my-topic --message "Hello, World!"
View function logs to observe the message.

Triggering a Function with Cloud Storage
Create a Pub/Sub topic and a bucket in Cloud Storage. Create a service account with Storage Admin privileges.

Trigger Function Code
Ensure cloud functions plugin in your pom.xml references the trigger function class inside functionTarget tags.

xml
Copy code
<groupId>com.google.cloud.functions</groupId>
<artifactId>function-maven-plugin</artifactId>
<version>${function-maven-plugin.version}</version>
<configuration>
    <functionTarget>com.gcp.examples.cloud.function.pubsub.trigger.PubsubTriggerFunction</functionTarget>
</configuration>
Deploy Function
bash
Copy code
gcloud functions deploy myq4-example \
    --gen2 \
    --entry-point=com.example.GCPexamples.example4.PubSubFunction \
    --runtime=java17 \
    --region=us-central1 \
    --trigger-topic=question4-topic \
    --run-service-account=gcp-bucket-service@gcp-examples-424113.iam.gserviceaccount.com \
    --source=. \
    --memory=512MB
Publish a message to the Pub/Sub topic with the file name and file content:

bash
Copy code
gcloud pubsub topics publish question4-topic --message="{\"fileName\":\"test1.txt\",\"fileContent\":\"Hello World\"}"
Open the Cloud Storage bucket on GCP to view the file with the ‘Hello World’ text.

Cleanup
Delete all services afterward to avoid extra charges.

