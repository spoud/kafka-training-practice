# Kafka Standby Mode

Sometimes we may want to have an application being on standby
(i.e. ready to receive traffic but not doing it while a primary on another site is up).
This project demonstrates a possible implementation with Spring Boot and [Unleash](https://www.getunleash.io/).
This demo leverages spring-kafka's ability to pause/resume consumers. All consumers can be paused/resumed from a single
central configuration class, thus making this solution portable across projects.

## How to run

1. Start dependencies with `docker-compose up`
1. Log into the Unleash UI at `http://localhost:4242` (user: `admin`, pw: `unleash4all`) and create the `standby` flag in the default project
1. Run `mvn compile spring-boot:run`
1. If the `standby` flag is enabled, the application will neither produce nor consume messages (attempts to produce will result in exceptions)
1. If the `standby` flag is disabled, the application will produce/consume messages

For details on how this is implemented, see `KafkaConfig.java`
