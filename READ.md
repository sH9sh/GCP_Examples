<h1>Google Cloud Functions Examples</h1>
    <p>This repository contains examples of deploying various types of Google Cloud Functions using Java. The examples include HTTP functions, scheduled functions, Pub/Sub functions, and storage-triggered functions.</p>
    
    <h2>Table of Contents</h2>
    <ul>
        <li><a href="#deploying-an-http-function">Deploying an HTTP Function</a></li>
        <li><a href="#deploying-a-scheduled-function">Deploying a Scheduled Function</a></li>
        <li><a href="#deploying-a-pubsub-function">Deploying a Pub/Sub Function</a></li>
        <li><a href="#triggering-a-function-with-cloud-storage">Triggering a Function with Cloud Storage</a></li>
    </ul>
    
    <h2>Prerequisites</h2>
    <ul>
        <li>Java 17</li>
        <li>Google Cloud SDK</li>
        <li>Maven</li>
        <li>A Google Cloud project</li>
    </ul>
    
    <h2 id="deploying-an-http-function">Deploying an HTTP Function</h2>
    <p>You can define a custom build process by adding a <code>cloudbuild.yaml</code> file. However, you can also deploy via <code>gcloud</code> command line without this.</p>
    
    <h3>Using <code>cloudbuild.yaml</code></h3>
    <p>Create a <code>cloudbuild.yaml</code> file:</p>
    <pre>
        <code>steps:
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
      - --entry-point=com.example.GCPexamples.example5.PubSubFunction5</code>
    </pre>
    <p>Trigger the build:</p>
    <pre><code>gcloud builds submit --config cloudbuild.yaml .</code></pre>
    
    <h3>Adding Code for the Function</h3>
    <p>The class will implement the <code>HttpFunction</code> interface. It will use the <code>service</code> method which has two parameters - <code>HttpRequest</code> and <code>HttpResponse</code>.</p>
    <pre>
        <code>package com.example.GCPexamples.example1;

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
}</code>
    </pre>
    <p>Ensure to add necessary dependencies like cloud functions. Also make sure to reference the function to be deployed under <code>function-maven-plugin</code> inside <code>functionTarget</code> tags.</p>
    
    <h3>Deploy Function via gcloud Command Line</h3>
    <pre><code>gcloud functions deploy HttpMethod \
    --entry-point=com.example.GCPexamples.example1.HttpMethod \
    --runtime=java17 \
    --trigger-http \
    --memory=512MB \
    --region=us-central1</code></pre>
    
    <h2 id="deploying-a-scheduled-function">Deploying a Scheduled Function</h2>
    <p>Add code for the scheduled function. This also implements the <code>HttpFunction</code> interface and uses the <code>service</code> method. Add code to log the current time when the function is triggered.</p>
    <pre>
        <code>package com.example.GCPexamples.example2;

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
}</code>
    </pre>
    <p>Deploy the function as done in <a href="#deploying-an-http-function">Deploying an HTTP Function</a>:</p>
    <pre><code>gcloud functions deploy ScheduledFunction \
    --entry-point=com.example.GCPexamples.example2.ScheduledFunction \
    --runtime=java17 \
    --trigger-http \
    --memory=512MB \
    --region=us-central1</code></pre>
    <p>Use Cloud Scheduler on GCP to trigger the function on a set time. Set target type as HTTP, HTTP method as OPTIONS. Add the URL of the deployed function. Hit create scheduler. The function will be triggered periodically as configured.</p>
    
    <h2 id="deploying-a-pubsub-function">Deploying a Pub/Sub Function</h2>
    <p>Create a topic and add a subscription to the topic.</p>
    
    <h3>Pub/Sub Function Code</h3>
    <pre>
        <code>package com.example.GCPexamples.example3;

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
}</code>
    </pre>
    
    <h3>PubSubMessage DTO</h3>
    <pre>
        <code>package com.example.GCPexamples.example3;

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
}</code>
    </pre>
    <p>Run <code>maven clean package</code> to generate a new JAR file.</p>
    
    <h3>Deploy Function</h3>
    <pre><code>gcloud functions deploy pubSub-function \
    --entry-point=com.example.GCPexamples.example3.PubSub \
    --runtime=java17 \
    --trigger-topic=AppTopic \
    --allow-unauthenticated \
    --gen2 \
    --source=target</code></pre>
    <p>Publish a message:</p>
    <pre><code>gcloud pubsub topics publish my-topic --message "Hello, World!"</code></pre>
    <p>View function logs to observe the message.</p>
    
    <h2 id="triggering-a-function-with-cloud-storage">Triggering a Function with Cloud Storage</h2>
    <p>Create a Pub/Sub topic and a bucket in Cloud Storage. Create a service account with Storage Admin privileges.</p>
    
    <h3>Trigger Function Code</h3>
    <p>Ensure the cloud functions plugin in your <code>pom.xml</code> references the trigger function class inside <code>functionTarget</code> tags.</p>
    <pre>
        <code>&lt;groupId&gt;com.google.cloud.functions&lt;/groupId&gt;
&lt;artifactId&gt;function-maven-plugin&lt;/artifactId&gt;
&lt;version&gt;${function-maven-plugin.version}&lt;/version&gt;
&lt;configuration&gt;
    &
