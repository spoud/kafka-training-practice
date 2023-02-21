# Exercise 3.1 - Java Client

* Create a new java project
* Add the following dependencies to your pom.xml:


[source,xml]
----
<dependency>
    <groupId>org.apache.kafka</groupId>
    <artifactId>kafka-clients</artifactId>
    <version>3.4.0</version>
</dependency>
----

image::assets/frank-gif.gif[width=400, float='left']

// TODO should we use the driver positions thing here?
* Create a producer that sends some messages to a topic on your local kafka instance
* Verify that the messages are in the topic with the command line tools
* Create a consumer that consumes the messages from the topic
* Extend the message by a key
* make sure you can serve 3 consumers with the same topic and the same group id
* remember the last value of each key and print it out when updated
* implement a rebalance listener, so you can clear the list of keys when a rebalance happens

// TODO should we include testing?
