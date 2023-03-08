# 4.1 Quarkus client exercises

You find the following two services in this folder:

* `quarkus-producer-from-rest` - a Quarkus REST server and Kafka producer
* `quarkus-consumer-to-rest` - a Quarkus Kafka consumer and REST client



## Produce

* Write a simple REST endpoint that sends a message to a Kafka topic
* Make sure you send a key and a value with the message
* Make sure the REST service is responding with error if the message could not be sent to Kafka
* Create tests which verifies the logic

## Try out Kafka Dev UI

* navigate to http://localhost:7981/q/dev/io.quarkus.quarkus-kafka-client/kafka-dev-ui and try out the Dev UI

## Check the health status of your application

* navigate to http://localhost:7981/q/health and check the health status of your application
* bring the Kafka broker down and check the health status again `docker-compose stop broker`
* try to send a message and watch the console log
* bring the Kafka broker up again and check the health status again `docker-compose start broker`

## Consume

* Write a consumer that calls an external REST API for each message (use the `Crappy echo service` under `resources/services/crappy-echo-service`)
* If your consumer gets stuck check the health endpoint... what is the problem?
* Try out `@Retry`
* Create tests which verifies the logic
* Navigate to http://localhost:7983/q/metrics and check what metrics are available for kafka and especially for the poll loop


## Extra challenge 1

* Remove the `@Retry` annotation
* Experiment with `failuer-strategy` `fail` and `ignore`
* Experiment with `failuer-strategy` dead-letter-topic

## Extra challenge 2

* Make sure you can run 3 consumers in parallel
* Create a REST endpoint on consumer where you return the last 10 messages per partition
* Make sure the local in memory state store you are using is cleaned up after a rebalance
* Secondary instances must be run with different ports e.g. `QUARKUS_HTTP_PORT=9000 quarkus dev`


# Generate a default quarkus project

make sure you have quarkus cli installed: https://quarkus.io/guides/cli-tooling

Run the following command to create a new project:

```bash
quarkus create app quarkus-producer-from-rest
cd quarkus-producer-from-rest
quarkus extension add smallrye-reactive-messaging-kafka
quarkus extension add quarkus-resteasy-reactive
quarkus extension add quarkus-smallrye-health
```

Next add the project as maven project into intellij (right click on `pom.xml`).

Add the following dependency to the `pom.xml`:

```xml

    <dependency>
      <groupId>io.quarkus</groupId>
      <artifactId>quarkus-test-kafka-companion</artifactId>
      <scope>test</scope>
    </dependency> 
```

Add the following properties to the `application.properties`:

```properties
# enable to use the local kafka broker instead of the dev services
%dev.kafka.bootstrap.servers=localhost:9092
%prod.kafka.bootstrap.servers=localhost:9092

# set ports which are not used by other services
quarkus.http.port=7988
quarkus.http.test-port=7989
```
