# Kafka training

## Get your Kafka practice in hands-on exercises and demos

### Structure of this repository

#### Folder `kafka-exercises`

In this folder you find exercises with some quests to solve.

#### Folder `kafka-exercises-solutions`

Here you find the solutions for the exercises.

#### Folder `kafka-demos`

In this folder you find demos to show how to use Kafka.

#### Folder `resources`

In this folder you find some resources to use in the exercises and demos in a central place.

### Startup Kafka cluster

```bash
docker-compose up -d
```

## Ports used

| Service                  | Port                          |
|--------------------------|-------------------------------|
| Confluent Control Center | [9021](http://localhost:9021) |
| Kafka                    | [9092](http://localhost:9092) |
| Schema Registry          | [8081](http://localhost:8081) |
| Kafka REST Proxy         | [8082](http://localhost:8082) |
| Kafka Connect            | [8083](http://localhost:8083) |
| KsqlDB Server            | [8088](http://localhost:8088) |
| Redpanda console         | [8080](http://localhost:8080) |
| AKHQ                     | [8089](http://localhost:8089) |
| Crappy echo service      | [7980](http://localhost:7980) |
| Quarkus http server      | [7981](http://localhost:7981) |
| Quarkus test server      | [7982](http://localhost:7982) |
| Quarkus http server 2    | [7983](http://localhost:7983) |
| Quarkus test server 2    | [7984](http://localhost:7984) |
| Kibana                   | [5601](http://localhost:5601) |
| Elasticsearch            | [9200](http://localhost:9200) |
| Flink Dashboard          | [8090](http://localhost:8090) |
