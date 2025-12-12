# Kafka Strems with Quarkus

This project uses Quarkus to configure
- a producer which generates random temperature readings for weather stations to
  - weather-stations topic: topic which contains info about all available weather stations
  - temperature-values topic: a topic with sensor readings from these stations
- a kafka streams topology to enrich temperature readings with station information and calculate aggregations for each station
  - temperatures-aggregated topic: contains the aggregations for each station

The project is based on the streams example from https://github.com/quarkusio/quarkus-quickstarts which is licensed under the Apache License.

## Running the application in dev mode

Start the confluent  docker compose services if they aren't running.

You can run your application in dev mode that enables live coding using:

```shell script
./mvnw compile quarkus:dev
```

You can then check the output topic with 

`kafka-avro-console-consumer --bootstrap-server localhost:9092 --from-beginning --topic temperatures-aggregated --property schema.registry.url=http://localhost:8081 | jq
`

## Generated Sources

If your IDE does not recognize the Avro generated classes, then explicitly mark the
folder `target/generated-sources/avsc` as `generated-sources root` in your IDE.
This will ensure the classes are in the classpath.

## Related Guides

- Confluent Schema Registry - Avro ([guide](https://quarkus.io/guides/kafka-schema-registry-avro)): Use Confluent as
  Avro schema registry
- Apache Kafka Client ([guide](https://quarkus.io/guides/kafka)): Connect to Apache Kafka with its native API
- Apache Avro ([guide](https://quarkus.io/guides/kafka)): Provide support for the Avro data serialization system
- Apache Kafka Streams ([guide](https://quarkus.io/guides/kafka-streams)): Implement stream processing applications
  based on Apache Kafka
