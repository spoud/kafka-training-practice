# 4.1 Quarkus client exercises

You find the following two services in this folder:

* `quarkus-producer-from-rest` - a Quarkus REST server and Kafka producer
* `quarkus-consumer-to-rest` - a Quarkus Kafka consumer and REST client



## Produce

* Write a simple REST endpoint that sends a message to a Kafka topic
* Make sure you send a key and a value with the message
* Make sure the REST service is responding with error if the message could not be sent to Kafka
* Create tests which verifies the logic

## Consume

* Write a consumer that calls an external REST API for each message
* Handle failures
* Create tests which verifies the logic

## Extra challenge

* Make sure you can run 3 consumers in parallel
* Create a REST endpoint on consumer where you return the last 10 messages per partition
* Make sure the local in memory state store you are using is cleaned up after a rebalance
* Create tests which verifies the logic


# Generate a default quarkus project

make sure you have quarkus cli installed: https://quarkus.io/guides/cli-tooling

Run the following command to create a new project:

```bash
quarkus create app quarkus-producer-from-rest
cd quarkus-producer-from-rest
quarkus extension add smallrye-reactive-messaging-kafka
quarkus extension add quarkus-resteasy-reactive
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
quarkus.http.port=7981
quarkus.http.test-port=7982
```