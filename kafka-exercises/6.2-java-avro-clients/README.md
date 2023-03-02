# Java CLients with AVRO

In this exercise you will work with

* a plain Java producer, configured to send AVRO records
* a quarkus smallrye consumer which deserializes these AVRO messages

## Setup

Make sure the kafka services are running with `docker-compose up -d`


## Step 1 - Implementing java-avro-producer

1. Review the `java-avro-producer/pom.xml`  
   Which AVRO related dependencies are listed?
2. Try to run `mvn clean package`. You will run into an issue, because the classes `Employee` and `EmployeeId` don't exist. They should be generated from our AVRO files in `src/main/avro`.
3. Add the missing plugin configuration in the build/plugins section of the `pom.xml` and retry.

       <plugin>
           <groupId>org.apache.avro</groupId>
           <artifactId>avro-maven-plugin</artifactId>
           <version>${avro.version}</version>
           <executions>
               <execution>
                   <phase>generate-sources</phase>
                   <goals>
                       <goal>schema</goal>
                   </goals>
                   <configuration>
                       <sourceDirectory>${project.basedir}/src/main/avro/</sourceDirectory>
                       <stringType>String</stringType>
                   </configuration>
               </execution>
           </executions>
       </plugin>

4. The generated AVRO classes should be in `target/generated-sources/avro`.
   Hint: You may need to mark this directory as `generated-sources-root` in your IDE to make sure it's in the projects classpath.
5. Proceed to implement the `//TODO` markers in [Producer.java](java-avro-producer/src/main/java/io/spoud/training/Producer.java)
6. Run the main method and watch the console log to verify that messages can be serialized and sent to the topic.


## Step 2 - Configuring the Quarkus Consumer

The quarkus consumer should receive employee objects and check their seniority to celebrate anniversaries.
Unfortunately the configuration and some annotations got lost...

1. Review the files `pom.xml` and `EmployeeAvroConsumer` in the project.
2. The `EmployeeAvroConsumer` is not currently receiving any messages. Add the missing annotation to listen for incoming messages. (`@Incoming("employees-avro")`)
3. Try to run the application. What errors can you see in the application log?
4. Add missing configuration entries in `application.properties` to use the KafkaAvroDeserializer for record keys and values.
5. Try to run the application. Are there more/other issues in the console?  
   The KafkaAvroDeserializer needs to know the URL of the Schema Registry. You can add this configuration with `mp.messaging.connector.smallrye-kafka.schema.registry.url=http://localhost:8081`


# Bonus

* Stop the producer and keep the consumer running
* Send a message to the topic `employees-avro` using the following command:
  `kafka-console-producer --topic employees-avro --bootstrap-server localhost:9092`  
  Then type a string, such as "employee" and hit enter to send the message.
* What happened to the consumer when it tried to deserialize this message?
