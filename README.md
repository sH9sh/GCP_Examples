# Question 1

You can define a custom build process by adding a cloudbuild.yaml file. However you can also deploy via gcloud command line without this.
This can be done by adding cloudbuild.yaml file.
`steps:
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
      - --entry-point com.example.GCPexamples.example5.PubSubFunction5`

This can then be triggered by the below command:
`gcloud builds submit --config cloudbuild.yaml .`
<br/>

Otherwise, add code for the function to be deployed. The class will implement the HttpFunction interface. It will use the service method which has two parameters - HttpRequest and HttpResponse.
`public class HttpMethod implements HttpFunction {
    private static final Logger logger = Logger.getLogger(ScheduledFunction.class.getName());
    @Override
    public void service(HttpRequest request, HttpResponse response) throws Exception {

        LocalDateTime currentTime = LocalDateTime.now();
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");
        String formattedTime = currentTime.format(formatter);

        logger.info("Current Time: " + formattedTime);
    }}`

Ensure to add necessary dependencies like cloud functions. Also make sure to reference the function to be deployed under 'function-maven-plugin' inside functionTarget tags.

Deploy function via gcloud command line:
`gcloud functions deploy HttpMethod
    --entry-point com.example.GCPexamples.example1.HttpMethod
    --runtime java17
    --trigger-http
    --memory 512MB
    --region us-central1`
<br/>
<br/>

# Question 2
<br/>
Add code for scheduled function. This also implements the HttpFunction interface and uses the service method. Add code to log the current time when the function is triggered.
`public class ScheduledFunction implements HttpFunction {

    private static final Logger logger = Logger.getLogger(ScheduledFunction.class.getName());
    @Override
    public void service(HttpRequest request, HttpResponse response){

        LocalDateTime currentTime = LocalDateTime.now();
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");
        String formattedTime = currentTime.format(formatter);

        logger.info("Current Time: " + formattedTime);
    }
}`
<br/>
Deploy function as is done in question 1.
`gcloud functions deploy HttpMethod
    --entry-point com.example.GCPexamples.example1.HttpMethod
    --runtime java17
    --trigger-http
    --memory 512MB
    --region us-central1`
    <br/>
Use cloud scheduler on GCP to trigger the function on a set time. Set target type as HTTP, HTTP method as OPTIONS. Add URL of deployed function.
Hit create scheduler.

The function will be triggered periodically as was configured.
<br/>
<br/>

# Question 3

Create a topic.

Add a subscription to topic.

`Add code to accept a pubSub message. 


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
}`


Add a PubSubMessage DTO class. 


`package com.example.GCPexamples.example3;

import java.util.Map;

public class PubSubMessage {
    	private String data;

    	private Map<String, String> attributes;

    	private String messageId;

    	private String publishTime;

    	public String getData(){
        return data;}

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
        this.messageId = messageId;}}`


Run maven clean package to generate new JAR file.

Deploy function with package and class name 


<code>
	gcloud functions deploy pubSub-function 
	--entry-point com.example.GCPexamples.example3.PubSub 
	--runtime java17 
	--trigger-topic AppTopic 
	--allow-unauthenticated 
	--gen2 
	--source=target
</code>

<br/>
Publish message with command below 

<code>
	gcloud pubsub topics publish my-topic --message "Hello, World!"
	View function logs to observe message
</code>
<br/>

# Question 4

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

# Question 5
This question involved writing a function triggered by pubsub topic which reads a JSON message. This message should be read to Cloud Storage as well as Big Query. A Big Query table needed to be created with schema.

I followed example 5 from here [neueda/gcp examples](https://github.com/neueda/gcp-examples)

I included model classes for node data (this also included schema for BQ table), pubsub body and pubsub message. Utils that handled serialising and deserialising Local Date Time. This converts Local Date Time to and from JSON. A Big query handler for creating a table, writing node data to BQ and creating an input stream. A storage handler to check if a bucket exists in cloud storage and to create one if not.

Create PubSub topic on GCP console.

Create service account on console with Storage Admin annd Big Query Data Editor permissions.

Creare data-set in BQ on console.

Deploy function to GCP.

Send message on pubsub topic.
`gcloud pubsub topics publish question5-topic --message="{\"nodeName\":\"node1\",\"timeStamp\":\"2023-09-25T14:53:45Z\",\"uplink\":1.23,\"downlink\":3.45}"`

Read function logs.
`gcloud functions logs read scenario-5-cloud-function-pubsub-trigger-java-jatin --gen2 --limit=20`

Check file is in Cloud storage and BQ.

Delete all services to avoid extra charges.

