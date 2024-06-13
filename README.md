<b>Question 4</b>

On GCP console - Create Pub/Sub topic.

Create bucket in Cloud Storage.

Create service account with Storage Admin privileges.

Add code for trigger function. Have the function class implement CloudEventsFunction. Include model classes for file details, pubsub message and pubsub body. See https://github.com/neueda/gcp-examples if any issues.

Ensure cloud functions plugin in your pom.xml references the trigger function class inside functionTarget tags.


<plugin>
	<groupId>com.google.cloud.functions</groupId>
	<artifactId>function-maven-plugin</artifactId>
	<version>${function-maven-plugin.version}</version>
		<configuration>
			<functionTarget>com.gcp.examples.cloud.function.pubsub.trigger.PubsubTriggerFunction</functionTarget>
		</configuration>

			</plugin>

Deploy function inside your folder project on CLI.
Edit --entry point, --trigger-topic, --run-service-account to your own names.

gcloud functions deploy myq4-example --gen2 --entry-point com.example.GCPexamples.example4.PubSubFunction --runtime java17 --region=us-central1 --trigger-topic=question4-topic --run-service-account=gcp-bucket-service@gcp-examples-424113.iam.gserviceaccount.com --source=. --memory 512MB

Here if you set ‘--source=target’ this will throw an error ‘Unable to load instance of class’. Ensure --source=.

Publish message to pubsub topic with file-name and file content.


gcloud pubsub topics publish question4-topic --message="{\"fileName\":\"test1.txt\",\"fileContent\":\"Hello World\"}"

Open cloud storage bucket on GCP to view file with ‘Hello World’ text.

Delete all services afterwards to avoid extra charges.
