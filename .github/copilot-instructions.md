# Kafka training repository instructions

## Build, test, and lint commands

- Root builds only the modules currently listed in the parent reactor. By default that is a subset of demos plus `resources/services/crappy-echo-service`.
  ```bash
  mvn clean verify
  ```

- CI uses batch mode from the repo root and then builds selected solution modules individually:
  ```bash
  mvn -B clean verify
  ```

- Build one reactor module from the root:
  ```bash
  mvn -pl kafka-demos/quarkus-streams clean verify
  ```

- Exercise and solution projects are usually built from their own directory, because most of them are not active modules in the root `pom.xml`:
  ```bash
  cd kafka-exercises/3.1-java-client/java-producer && mvn clean verify
  cd kafka-exercises-solutions/4.1-quarkus/quarkus-consume-call-rest && mvn clean verify
  ```

- Run a single Maven test in a plain Java module:
  ```bash
  cd kafka-exercises/3.1-java-client/java-producer && mvn -Dtest=ProducerAppTest test
  ```

- Run a single Maven test in a Quarkus module:
  ```bash
  cd kafka-exercises/4.1-quarkus/quarkus-producer-from-rest && ./mvnw -Dtest=KafkaPingResourceTest test
  ```

- Quarkus modules that include the wrapper are typically run in dev mode from the module directory:
  ```bash
  ./mvnw compile quarkus:dev
  ```

- No dedicated lint command is defined in the repository. I did not find Checkstyle, Spotless, PMD, or SpotBugs configured in the Maven builds.

## High-level architecture

- The repository has three main code areas:
  - `kafka-demos/`: runnable examples and experiments
  - `kafka-exercises/`: intentionally incomplete training tasks
  - `kafka-exercises-solutions/`: completed reference implementations that mirror the exercise layout

- The root `pom.xml` is a parent reactor, but it does **not** include every project in the repository. It centralizes shared Java, Kafka, Quarkus, Confluent, logging, and test dependency versions for the modules that inherit from `training-parent`. Many exercise and solution projects are standalone Maven builds outside the active root reactor.

- `start.sh` and `stop.sh` are the operational entrypoints for the local training environment. They layer the base `docker-compose.yml` together with addon compose files such as AKHQ, Redpanda Console, ELK, Flink, and kcat. The scripts support both `docker-compose` and `docker compose`, and `start.sh` will download the base `docker-compose.yml` if it is missing.

- `resources/` holds shared assets used by demos and exercises:
  - `resources/services/crappy-echo-service/` is a Quarkus service used by the Quarkus consumer exercise
  - `resources/files/` and related folders contain reusable data and schemas

- The repository mixes several implementation styles:
  - simple Java Kafka client demos and exercises
  - Quarkus services using reactive messaging, Kafka companion testing, and sometimes Avro/Schema Registry
  - a few standalone Spring Boot JSON Schema demos under `kafka-demos/` that do not inherit from the root parent

- CI reflects that split architecture. `.github/workflows/build.yaml` runs `mvn -B clean verify` at the root, then separately builds selected modules from `kafka-exercises-solutions/` by `working-directory`.

## Key conventions

- Treat `kafka-exercises/` and `kafka-exercises-solutions/` as paired trees. Before changing an exercise, check whether the corresponding solution already demonstrates the intended end state or test style.

- Do not assume every subproject inherits from the root parent. Parent-based demos and shared services use `io.spoud.training:training-parent`, but some exercises and Spring Boot demos define their own standalone POMs and versions.

- The root reactor is intentionally selective. If you add a new parent-based module and expect it to build in `mvn clean verify` from the repo root, it must be added to the root `<modules>` list. Otherwise, build it from its own directory and update CI only if needed.

- Kafka and Confluent versions are intentionally separated in the root parent. The comment on `kafka.version` matters: use an Apache Kafka OSS version there, not a Confluent Platform version.

- Quarkus projects consistently reserve fixed local ports to avoid collisions with the compose stack:
  - `7980` for `crappy-echo-service`
  - `7981` / `7982` for producer app HTTP and test ports
  - `7983` / `7984` for consumer app HTTP and test ports
  Keep that port scheme when adding sibling services.

- Quarkus modules commonly pin local Kafka access in `application.properties` with `%dev.kafka.bootstrap.servers=localhost:9092` and `%prod.kafka.bootstrap.servers=localhost:9092`, while also keeping `quarkus.kafka.devservices.image-name=redpandadata/redpanda` as a dev-services fallback. Prefer matching the existing pattern in the module you are editing instead of normalizing all modules.

- Test style depends on module type:
  - plain Java Kafka exercises use Testcontainers directly, for example `ConfluentKafkaContainer`
  - Quarkus modules use `@QuarkusTest`, `@QuarkusTestResource(KafkaCompanionResource.class)`, and `@InjectKafkaCompanion`
  When adding tests, follow the style already used by the matching exercise or solution module.

- Quarkus modules often rely on generated sources and plugin-managed code generation. In `kafka-demos/quarkus-streams`, generated Avro classes end up under `target/generated-sources/avsc`; avoid replacing that flow with ad hoc generated code.

- The repository is organized around local, hands-on Kafka workflows. For app changes that depend on a broker, schema registry, or REST sidecar, prefer the existing local compose stack and the documented ports from `README.md` instead of inventing alternate infrastructure.
