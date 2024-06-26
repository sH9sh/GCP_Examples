<b>Question 3</b>

Create a topic.

Add a subscription to topic.

Add code to accept a pubSub message. 

<code>
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
</code>

Add a PubSubMessage DTO class. 


<code>
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
</code>

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



<b>Question 4</b>

On GCP console - Create Pub/Sub topic.

Create bucket in Cloud Storage.

Create service account with Storage Admin privileges.

Add code for trigger function. Have the function class implement CloudEventsFunction. Include model classes for file details, pubsub message and pubsub body. See https://github.com/neueda/gcp-examples if any issues.

Ensure cloud functions plugin in your pom.xml references the trigger function class inside functionTarget tags.

	<groupId>com.google.cloud.functions</groupId>
	<artifactId>function-maven-plugin</artifactId>
	<version>${function-maven-plugin.version}</version>
		<configuration>
			<functionTarget>com.gcp.examples.cloud.function.pubsub.trigger.PubsubTriggerFunction</functionTarget>
		</configuration>

Deploy function inside your folder project on CLI.
Edit --entry point, --trigger-topic, --run-service-account to your own names.

<code>gcloud functions deploy myq4-example --gen2 --entry-point com.example.GCPexamples.example4.PubSubFunction --runtime java17 --region=us-central1 --trigger-topic=question4-topic --run-service-account=gcp-bucket-service@gcp-examples-424113.iam.gserviceaccount.com --source=. --memory 512MB</code>

Here if you set ‘--source=target’ this will throw an error ‘Unable to load instance of class’. Ensure --source=.

Publish message to pubsub topic with file-name and file content.


<code>gcloud pubsub topics publish question4-topic --message="{\"fileName\":\"test1.txt\",\"fileContent\":\"Hello World\"}"</code>

Open cloud storage bucket on GCP to view file with ‘Hello World’ text.

Delete all services afterwards to avoid extra charges.
