# Internal exercise and demo validation

This document is the reproducible source of truth for validating `kafka-exercises/`, `kafka-exercises-solutions/`, and the runnable material in `kafka-demos/`.

It is intended for maintainers and automation, not for trainees working through the lessons.

## Goals

- verify that the code-backed exercises still behave as intended after dependency, Docker, or workflow updates
- verify that markdown-guided lessons still work against the current container images
- verify that the runnable demos still build and that the Docker-backed demos still work against the current images
- keep `kafka-exercises/` and `kafka-exercises-solutions/` aligned except for the intended learner-facing gaps

## Inventory

| Area | Exercise | Solution | Validation mode |
| --- | --- | --- | --- |
| 2.1 CLI | `kafka-exercises/2.1-cli` | `kafka-exercises-solutions/2.1-cli` | scripted runtime + docs |
| 3.1 Java client | `kafka-exercises/3.1-java-client` | `kafka-exercises-solutions/3.1-java-client` | Maven build/test + pair diff |
| 4.1 Quarkus | `kafka-exercises/4.1-quarkus` | `kafka-exercises-solutions/4.1-quarkus` | Maven build/test + pair diff |
| 6.1 Schema Registry | `kafka-exercises/6.1-schema-registry` | `kafka-exercises-solutions/6.1-schema-registry` | scripted runtime + docs |
| 6.2 Java AVRO clients | `kafka-exercises/6.2-java-avro-clients` | `kafka-exercises-solutions/6.2-java-avro-clients` | Maven build/test + pair diff |

### Demo inventory

| Area | Path | Validation mode |
| --- | --- | --- |
| Root reactor demos | `kafka-demos/java-batching`, `java-consumer-liveness`, `java-default-partitioning`, `java-read-avro-message-from-file`, `quarkus-streams` | root Maven build |
| Standalone demo modules | `kafka-demos/RecordNameStrategy`, `quarkus-producer-avro-and-headers`, `jsonschema-demo`, `jsonschema-spring-boot-demo`, `flink/datastream` | per-module Maven build |
| Docker-backed demo | `kafka-demos/kafka-connect-pipeline/postgres-mysql` | scripted Docker runtime |
| Runbook/manual demos | `kafka-demos/clear-topic`, `delete-records`, `cli-examples`, `AdminKraft`, `kafka-connect-pipeline/datagen-elasticsearch`, `flink/README.md` | manual runbook / docs review |

## Validation rules

- Do not copy solution code into the exercise tree just to make the exercise pass.
- Accept only the intended training gaps: missing implementation, missing configuration, missing generated-source plugin wiring, and failing tests that explicitly point at the TODO.
- Treat stale dependency versions, broken README commands, and missing solution-side documentation as drift and fix them.
- Treat demo build failures, stale demo tests, missing imports, and broken compose/doc commands as repo drift and fix them.

## Reproducible process

### 1. Build and test the code-backed modules

Run the root build first:

```bash
mvn -B clean verify
```

Then validate the exercise and solution modules individually from their own directories:

```bash
cd kafka-exercises/3.1-java-client/java-producer && mvn clean verify
cd kafka-exercises/3.1-java-client/java-consumer && mvn clean verify
cd kafka-exercises/4.1-quarkus/quarkus-producer-from-rest && mvn clean verify
cd kafka-exercises/4.1-quarkus/quarkus-consume-call-rest && mvn clean verify
cd kafka-exercises/6.2-java-avro-clients/java-avro-producer && mvn clean verify
cd kafka-exercises/6.2-java-avro-clients/quarkus-avro-consumer && mvn clean verify

cd kafka-exercises-solutions/3.1-java-client/java-producer && mvn clean verify
cd kafka-exercises-solutions/3.1-java-client/java-consumer && mvn clean verify
cd kafka-exercises-solutions/4.1-quarkus/quarkus-producer-from-rest && mvn clean verify
cd kafka-exercises-solutions/4.1-quarkus/quarkus-consume-call-rest && mvn clean verify
cd kafka-exercises-solutions/6.2-java-avro-clients/java-avro-producer && mvn clean verify
cd kafka-exercises-solutions/6.2-java-avro-clients/quarkus-avro-consumer && mvn clean verify
```

Interpretation:

- exercise modules that are intentionally incomplete should fail for the expected TODO reason
- solution modules should pass
- if an exercise fails for a reason unrelated to the lesson, or a solution fails, treat that as repo drift

### 2. Build the standalone demo modules

The root reactor does not cover every demo. Validate the remaining buildable demos from their own directory:

```bash
cd kafka-demos/RecordNameStrategy && mvn clean verify
cd kafka-demos/quarkus-producer-avro-and-headers && mvn clean verify
cd kafka-demos/jsonschema-demo && mvn clean verify
cd kafka-demos/jsonschema-spring-boot-demo && mvn clean verify
cd kafka-demos/flink/datastream && mvn clean verify
```

Interpretation:

- these demos should all build successfully
- if a demo no longer builds because of stale tests, missing imports, or dependency drift, fix that as repo drift

### 3. Verify the markdown lessons with an isolated stack

The root compose stack uses fixed host ports and fixed container names. If `9092`, `8081`, or `8088` are already occupied, use a temporary three-service stack for verification instead of disrupting the existing environment.

Use this minimal temporary compose file:

```yaml
services:
  broker:
    image: confluentinc/cp-kafka:8.2.0
    hostname: broker
    ports:
      - "19092:9092"
      - "19101:9101"
    environment:
      KAFKA_NODE_ID: 1
      KAFKA_LISTENER_SECURITY_PROTOCOL_MAP: 'CONTROLLER:PLAINTEXT,PLAINTEXT:PLAINTEXT,PLAINTEXT_HOST:PLAINTEXT'
      KAFKA_ADVERTISED_LISTENERS: 'PLAINTEXT://broker:29092,PLAINTEXT_HOST://localhost:9092'
      KAFKA_OFFSETS_TOPIC_REPLICATION_FACTOR: 1
      KAFKA_GROUP_INITIAL_REBALANCE_DELAY_MS: 0
      KAFKA_TRANSACTION_STATE_LOG_MIN_ISR: 1
      KAFKA_TRANSACTION_STATE_LOG_REPLICATION_FACTOR: 1
      KAFKA_JMX_PORT: 9101
      KAFKA_JMX_HOSTNAME: localhost
      KAFKA_PROCESS_ROLES: 'broker,controller'
      KAFKA_CONTROLLER_QUORUM_VOTERS: '1@broker:29093'
      KAFKA_LISTENERS: 'PLAINTEXT://broker:29092,CONTROLLER://broker:29093,PLAINTEXT_HOST://0.0.0.0:9092'
      KAFKA_INTER_BROKER_LISTENER_NAME: 'PLAINTEXT'
      KAFKA_CONTROLLER_LISTENER_NAMES: 'CONTROLLER'
      KAFKA_LOG_DIRS: '/tmp/kraft-combined-logs'
      CLUSTER_ID: 'MkU3OEVBNTcwNTJENDM2Qk'
  schema-registry:
    image: confluentinc/cp-schema-registry:8.2.0
    hostname: schema-registry
    depends_on:
      - broker
    ports:
      - "18081:8081"
    environment:
      SCHEMA_REGISTRY_HOST_NAME: schema-registry
      SCHEMA_REGISTRY_KAFKASTORE_BOOTSTRAP_SERVERS: 'broker:29092'
      SCHEMA_REGISTRY_LISTENERS: http://0.0.0.0:8081
  kcat:
    image: edenhill/kcat:1.7.1
    hostname: kcat
    depends_on:
      - broker
    entrypoint: /bin/sh
    command: -c 'tail -f /dev/null'
```

Start it with:

```bash
docker compose -p validation-mini -f /tmp/kafka-training-validation-minimal.yml up -d
```

Then run the 2.1 CLI lesson against namespaced topics such as `topic-3p-validation` and `scoreboard-validation`, and run the 6.1 Schema Registry lesson against namespaced subjects/topics such as `cars-validation-value` and `cars-validation`. This keeps reruns isolated and avoids polluting learner data.

Cleanup:

```bash
docker compose -p validation-mini -f /tmp/kafka-training-validation-minimal.yml down -v --remove-orphans
```

### 4. Verify the Docker-backed demo flows

For the Debezium CDC demo:

```bash
cd kafka-demos/kafka-connect-pipeline/postgres-mysql
docker compose up -d
docker compose exec mysql bash -lc 'mysql --user root --password=debezium < /setup/mysql-setup.sql'
```

Then verify:

- source connector reaches `RUNNING`
- sink connector reaches `RUNNING`
- the sink table exists and receives the updated email for customer `1003`

If you run this against a remote Docker context over SSH, remember:

- published ports may need to be remapped if the remote host already uses `9092`, `5432`, `3306`, or `8083`
- bind mounts refer to the remote filesystem, so local setup files such as `mysql-setup.sql` must be copied into the container with `docker cp` instead of relying on a local host mount

### 5. Compare each exercise tree with its solution tree

Focus on high-signal files:

- `pom.xml`
- `src/main/resources/application.properties`
- tests
- README files

Ignore:

- `target/`
- generated Dockerfiles when they were intentionally refreshed across both trees

The fastest initial comparison is:

```bash
diff -qr kafka-exercises/3.1-java-client kafka-exercises-solutions/3.1-java-client
diff -qr kafka-exercises/4.1-quarkus kafka-exercises-solutions/4.1-quarkus
diff -qr kafka-exercises/6.2-java-avro-clients kafka-exercises-solutions/6.2-java-avro-clients
```

Then inspect the meaningful diffs with `git diff --no-index`.

## Verification snapshot (2026-03-25)

### Automated build/test sweep

Root:

- `mvn -B clean verify` passed
- this covers the parent reactor demos: `java-batching`, `java-consumer-liveness`, `java-default-partitioning`, `java-read-avro-message-from-file`, `quarkus-streams`, plus `resources/services/crappy-echo-service`

Exercises:

- `3.1-java-client/java-producer` passed
- `3.1-java-client/java-consumer` passed
- `4.1-quarkus/quarkus-producer-from-rest` failed as expected because the exercise still returns an empty body instead of `ok pong-kafka json`
- `4.1-quarkus/quarkus-consume-call-rest` failed as expected because `PingMessage` is still intentionally missing
- `6.2-java-avro-clients/java-avro-producer` failed as expected because the AVRO classes are not generated until the learner adds the plugin
- `6.2-java-avro-clients/quarkus-avro-consumer` failed as expected because the Avro deserializer settings are still intentionally missing

Solutions:

- all six solution modules passed

### Demo build sweep

Standalone demos:

- `kafka-demos/RecordNameStrategy` passed
- `kafka-demos/quarkus-producer-avro-and-headers` passed after aligning the failure test with the current Avro endpoint/channel
- `kafka-demos/jsonschema-demo` passed after restoring the missing `org.springframework.kafka.support.KafkaNull` import
- `kafka-demos/jsonschema-spring-boot-demo` passed
- `kafka-demos/flink/datastream` passed

### Docker-backed demo verification

Verified successfully against the remote Docker context `desktop` using remapped published ports:

- `kafka-demos/kafka-connect-pipeline/postgres-mysql`
  - source connector reached `RUNNING`
  - sink connector reached `RUNNING`
  - sink table `inventory-source_inventory_customers` was found in schema `copy`
  - sink table contained 4 rows
  - the replicated row for customer `1003` contained `validation@email.com`

### Markdown lesson verification

Verified successfully against the isolated validation stack:

- 2.1 CLI
  - metadata lookup with `kcat`
  - topic create / describe
  - produce / consume
  - delete
  - compacted-topic creation
  - keyed scoreboard production
- 6.1 Schema Registry
  - list subjects
  - register schema v1 and v2
  - fetch stored schema text
  - reject incompatible v3 under default compatibility
  - allow v3 after switching the subject to `FORWARD`
  - verify version list
  - AVRO console producer / consumer round-trip

### Manual-only follow-up checks

These are still worth spot-checking when lesson behavior changes, but they were not browser-automated in this run:

- 2.1 Control Center visual inspection
- 2.1 observing compaction after waiting for inactive segments and cleaner work
- 4.1 Quarkus Dev UI page checks
- 4.1 manual health check while stopping and restarting the external broker
- `kafka-demos/clear-topic` and `kafka-demos/delete-records` CLI runbooks
- `kafka-demos/AdminKraft` and `kafka-demos/cli-examples`
- `kafka-demos/kafka-connect-pipeline/datagen-elasticsearch`
- the Flink SQL walkthrough in `kafka-demos/flink/README.md`
- runtime/API walkthroughs for the JSON Schema Spring Boot demos, which still assume user-provided Confluent Cloud credentials

### Drift review

Accepted gaps:

- TODO implementations in exercise source files
- missing AVRO code generation wiring in `kafka-exercises/6.2-java-avro-clients/java-avro-producer/pom.xml`
- missing consumer binding / deserializer configuration in the incomplete exercises

Fixed or documented drift during this pass:

- corrected the broken `docker compose -f docker compose.yml` command in the 2.1 CLI lesson docs
- corrected the same broken compose-file command pattern in `kafka-demos/RecordNameStrategy/README.md` and `kafka-demos/kafka-connect-pipeline/datagen-elasticsearch/README.md`
- added a missing solution-side README for 6.1 Schema Registry
- expanded the 6.2 solution README so the expected deltas are explicit
- aligned stale non-lesson version pins in the affected Quarkus / AVRO lesson POMs
- aligned the drifting standalone Kafka OSS client versions to Kafka `4.2.0`
- updated `start.sh` / `stop.sh` so their fallback compose download points at the current Confluent 8.2.0 stack
- restored the missing Spring Kafka `KafkaNull` import in `kafka-demos/jsonschema-demo`
- aligned the stale `quarkus-producer-avro-and-headers` failure test with the current Avro route/channel
- added `depends_on` ordering for the Debezium `postgres-mysql` demo so `connect` does not race the broker during startup
