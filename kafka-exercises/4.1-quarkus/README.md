
## Produce

* create a new quarkus project with
  * smallrye-reactive-messaging-kafka
  * quarkus-resteasy-reactive
* Write a simple REST endpoint that sends a message to a Kafka topic
* Make sure the REST service is responding with error if the message could not be sent to Kafka

## Consume

* create a new quarkus project with
  * smallrye-reactive-messaging-kafka
  * quarkus-resteasy-reactive
* Write a consumer that calls an external REST API
* Handle failures
