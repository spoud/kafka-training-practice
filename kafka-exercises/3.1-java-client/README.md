# Exercise 3.1 - Java Client

* Create a new java project
* Add the following dependencies to your pom.xml:


```xml
<dependency>
    <groupId>org.apache.kafka</groupId>
    <artifactId>kafka-clients</artifactId>
    <version>3.4.0</version>
</dependency>

```

* Create a producer that sends some messages to a topic on your local kafka instance
* Verify that the messages are in the topic with the command line tools
* Create a consumer that consumes the messages from the topic
* Extend the message by a key
* make sure you can serve 3 consumers with the same topic and the same group id
* remember the last value of each key and print it out when updated
* implement a rebalance listener, so you can clear the list of keys when a rebalance happens

Make sure logging is working by adding the following dependencies

```xml
    <dependency>
      <groupId>org.slf4j</groupId>
      <artifactId>slf4j-api</artifactId>
      <version>2.0.5</version>
      <scope>compile</scope>
    </dependency>

    <dependency>
      <groupId>org.slf4j</groupId>
      <artifactId>slf4j-simple</artifactId>
      <version>2.0.5</version>
      <scope>compile</scope>
    </dependency>
```
