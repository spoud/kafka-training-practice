# JsonSchema Demo with Kafka and Spring Boot

This repository contains a minimal Spring Boot project that demonstrates how to use Kafka with JsonSchema.
The scenario is quite simple: we provide a REST API that allows users to create, query and update movies.
Each movie is represented by a simple JSON object:

```json
{
  "title": "Matrix",
  "year": 1999,
  "Genre": "Sci-Fi"
}
```

We want to persist the movies in a compacted Kafka topic. When a new movie is created via REST API, we produce a message
to the Kafka topic. The producer is configured to use JsonSchema with Confluent Schema Registry.
The movie schema is automatically derived from the Java class `MovieModel`. It is also automatically registered in the Schema Registry.

To make sure that our Rest API remembers all created movies even after a restart, we also consume from the Kafka topic.
In this way, we can rebuild the state of the application by replaying all messages from the Kafka topic.
Thus, this demo also demonstrates how to configure a Kafka consumer with JsonSchema.

This repository is intended to serve as a reference for developers who want to use Kafka with JsonSchema in their Spring Boot applications.
For details, please view the source code and the comments contained therein.

> Note: the interesting parts of the code are located in `MovieService`, `KafkaConfiguration`, and `CommonKafkaProperties`.

## How to run

> *Note*: this guide assumes that you are using Kafka on Confluent Cloud.

Begin by creating Kafka and Schema Registry API keys for your application. Once done, either paste them into application.properties
or set the relevant environment variables

```shell
$ export KAFKA_BOOTSTRAP_SERVERS=... 
$ export KAFKA_API_KEY=...
$ ...
```

Finally, run the application by executing

```shell
mvn spring-boot:run
```

## How to test

You can test the application by sending HTTP requests to the REST API. Here are some examples:

```shell
# Create a new movie
$ curl -X POST http://localhost:7070/api/movie -H "Content-Type: application/json" -d '{"title": "Matrix", "year": 1999, "genre": "Sci-Fi"}'

# Get all movies
$ curl http://localhost:7070/api/movie
```

