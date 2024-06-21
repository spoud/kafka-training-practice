# JsonSchema Demo with Kafka and Spring Boot (multiple event types per topic)

This repository contains a minimal Spring Boot project that demonstrates how to use Kafka with JsonSchema.
We demonstrate using multiple event types per topic.
The scenario is quite simple: we provide a REST API that allows users to create, query and update movies and books.
Each movie is represented by a simple JSON object:

```json
{
  "title": "Matrix",
  "year": 1999,
  "genre": "Sci-Fi",
  "type": "movie"
}
```

Each book is represented as follows:

```json
{
    "type": "book",
    "title": "Harry Potter and the Philosopher's Stone",
    "year": 1997,
    "genre": "Fantasy",
    "author": "J. K. Rowling",
    "isbn": "978-0-7475-3269-9"
}
```

We want to persist the movies/books in a common compacted Kafka topic. When a new movie/book is created via REST API, we produce a message
to the Kafka topic. The producer is configured to use JsonSchema with Confluent Schema Registry (or any compatible schema registry, like Apicurio).
The book/movie schema is automatically derived from the Java class `MovieModel` or `BookModel`. It is also automatically registered in the Schema Registry.

To make sure that our Rest API remembers all created movies even after a restart, we also consume from the Kafka topic.
In this way, we can rebuild the state of the application by replaying all messages from the Kafka topic.
Thus, this demo also demonstrates how to configure a Kafka consumer with JsonSchema.

This repository is intended to serve as a reference for developers who want to use Kafka with JsonSchema in their Spring Boot applications
and need to handle multiple event types per topic.
For details, please view the source code and the comments contained therein.

> Note: the interesting parts of the code are located in `MovieService`, `KafkaConfiguration`, and `CommonKafkaProperties`.

For serialization/deserialization we leverage Jackson's [polymorphic deserialization feature](https://github.com/FasterXML/jackson-docs/wiki/JacksonPolymorphicDeserialization),
which allows Jackson to deserialize JSON objects into instances of the correct class based on some logic (in our case, based on the `type` field).

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
