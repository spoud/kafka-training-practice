# Java Clients with AVRO

Use the paired exercise README as the main walkthrough:

- `../../kafka-exercises/6.2-java-avro-clients/README.md`

The solution tree intentionally differs from the exercise tree only in the parts learners are expected to add or fix:

- `java-avro-producer/pom.xml`
  - adds the `avro-maven-plugin` so the AVRO classes are generated during `generate-sources`
- `java-avro-producer/src/main/java/io/spoud/training/Producer.java`
  - configures the AVRO serializers and builds valid `Employee` / `EmployeeId` records
- `quarkus-avro-consumer/src/main/java/io/spoud/training/EmployeeAvroConsumer.java`
  - adds the missing `@Incoming("employees-avro")` consumer method binding
- `quarkus-avro-consumer/src/main/resources/application.properties`
  - adds the missing Avro deserializer and Schema Registry settings

Validation:

```bash
cd kafka-exercises-solutions/6.2-java-avro-clients/java-avro-producer && mvn clean verify
cd kafka-exercises-solutions/6.2-java-avro-clients/quarkus-avro-consumer && mvn clean verify
```
