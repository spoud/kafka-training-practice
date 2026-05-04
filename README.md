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

### Starting the environment

> **New users:** run `./start.sh` to start the local Kafka environment. This is the recommended entrypoint — it handles both `docker-compose` and `docker compose` automatically and supports optional add-on services.

```bash
# Minimal start: Kafka broker + Schema Registry only
./start.sh

# With a web UI (choose one)
./start.sh --ui akhq        # AKHQ  →  http://localhost:8089
./start.sh --ui redpanda    # Redpanda Console  →  http://localhost:8084
./start.sh --ui confluent   # Confluent Control Center  →  http://localhost:9021

# Full stack: all services + all UIs
./start.sh --full

# Help / all options
./start.sh --help
```

After startup, the script prints the active URLs for your current configuration.

To stop all services:

```bash
./stop.sh
```

## Ports used

| Service                  | Port                          | Profile          |
|--------------------------|-------------------------------|------------------|
| Kafka                    | [9092](http://localhost:9092) | always on        |
| Schema Registry          | [8081](http://localhost:8081) | always on        |
| AKHQ                     | [8089](http://localhost:8089) | `--ui akhq`      |
| Redpanda Console         | [8084](http://localhost:8084) | `--ui redpanda`  |
| Confluent Control Center | [9021](http://localhost:9021) | `--ui confluent` |
| Kafka REST Proxy         | [8082](http://localhost:8082) | `--full`         |
| Kafka Connect            | [8083](http://localhost:8083) | `--full`         |
| KsqlDB Server            | [8088](http://localhost:8088) | `--full`         |
| Elasticsearch            | [9200](http://localhost:9200) | `--full`         |
| Kibana                   | [5601](http://localhost:5601) | `--full`         |
| Flink Dashboard          | [8090](http://localhost:8090) | `--full`         |
| Crappy echo service      | [7980](http://localhost:7980) | exercise         |
| Quarkus http server      | [7981](http://localhost:7981) | exercise         |
| Quarkus test server      | [7982](http://localhost:7982) | exercise         |
| Quarkus http server 2    | [7983](http://localhost:7983) | exercise         |
| Quarkus test server 2    | [7984](http://localhost:7984) | exercise         |

## Dependabot auto-merge

Dependabot PRs targeting the `updates` branch are auto-merged by GitHub Actions after the `Build aggregator` pull request run succeeds.

If a Dependabot PR changes files under `.github/workflows/`, the workflow needs a repository secret named `DEPENDABOT_AUTOMERGE_TOKEN` that contains a token with permission to update workflow files. Without that secret, non-workflow Dependabot PRs can still auto-merge, but workflow-changing PRs stay manual.
