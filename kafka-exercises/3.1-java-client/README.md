# Exercise 3.1 - Java Client

* Create a new Java project
* Add the following dependencies to your `pom.xml`:

```xml
<dependency>
    <groupId>org.apache.kafka</groupId>
    <artifactId>kafka-clients</artifactId>
    <version>3.9.0</version>
</dependency>
```

## Producer

* Create a producer that sends 100 messages (one every 10ms) to a topic on your local Kafka instance
* Verify that the messages are in the topic with the command line tools and [control center](http://localhost:9021/)
* Figure out what is the configuration for the producers' `client.id`
* Figure out how many round trips you got to the broker
* Set the `linger.ms` to 100 and verify it is working

## Consumer

* Create a consumer that consumes the messages from the topic and prints them out
* Extend the message by a key
* Make sure you can serve 3 consumers with the same topic and the same group id
* Remember the last value of each key and print it out when updated
* Implement a rebalance listener, so you can clear the list of keys when a rebalance happens

## Testing

* Create a test that verifies that the producer is sending the messages

## Bonus

* Create two topics with 3 partitions each
* Write messages to both topics with the same set of keys
* Make the consumers consume from both topics
* Start 5 consumers and watch the output
* Experiment with the partition assignment strategies

Make sure logging is working by adding the following dependencies

```xml
    <dependency>
      <groupId>org.slf4j</groupId>
      <artifactId>slf4j-api</artifactId>
      <version>2.0.16</version>
      <scope>compile</scope>
    </dependency>

    <dependency>
      <groupId>org.slf4j</groupId>
      <artifactId>slf4j-simple</artifactId>
      <version>2.0.16</version>
      <scope>compile</scope>
    </dependency>
```
