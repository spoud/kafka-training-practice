# Schema Registry API Exercise

* Confluent Schema Registry provides a RESTful API for managing schemas and subjects
* Confluent Schema Registry documentation: <https://docs.confluent.io/platform/current/schema-registry/index.html>
* API documentation: <https://docs.confluent.io/platform/current/schema-registry/develop/api.html>

## Preparation

* Start the required docker compose services with `docker-compose up -d broker schema-registry`
* **Note**: If you don't have `curl` installed on your PC, then you can also execute these commands inside the schema-registry container:
`docker-compose exec schema-registry bash`
* **Note**: The `kafka-avro-console-producer` and `kafka-avro-console-consumer` tools are installed in the `schema-registry` container.
You can access it with `docker-compose exec schema-registry bash` and then execute the commands.

## Exercise - Schema Registry API

1. Display all currently available subjects in the Schema Registry  

  ```bash
  curl -w "\n" http://localhost:8081/subjects
  ```

2. We want to register a new schema for record values in topic `cars`

  ```bash
  curl -w "\n" -X POST -H "Content-Type: application/vnd.schemaregistry.v1+json" \
  -d '{"schema": "{\"type\": \"record\", \"name\": \"Car\", \"namespace\": \"io.spoud.training\", \"fields\": [{\"name\": \"make\", \"type\": \"string\"}, {\"name\": \"model\", \"type\": \"string\"}]}", "metadata": {"properties": {"application.major.version": "2"}}}' \
  http://localhost:8081/subjects/cars-value/versions
  ```

3. Display the registered schema with `curl -w "\n" http://localhost:8081/subjects/cars-value/versions/1/schema`

4. Version 2 of our schema contains a new field "price". Register this new version.

  ```bash
  curl -w "\n" -X POST -H "Content-Type: application/vnd.schemaregistry.v1+json" \
  -d '{"schema": "{\"type\": \"record\", \"name\": \"Car\", \"namespace\": \"io.spoud.training\", \"fields\": [{\"name\": \"make\", \"type\": \"string\"}, {\"name\": \"model\", \"type\": \"string\"}, {\"name\": \"price\", \"type\": \"int\", \"default\": 0}]}"}' \
  http://localhost:8081/subjects/cars-value/versions
  ```

5. Produce a message to the topic with this schema:

  ```bash
  kafka-avro-console-producer --bootstrap-server broker:29092 --property schema.registry.url=http://localhost:8081 --topic cars \
  --property value.schema='{"type": "record", "name": "Car", "namespace": "io.spoud.training", "fields": [{"name": "make", "type": "string"}, {"name": "model", "type": "string"}, {"name": "price", "type": "int", "default":  0}]}'
  ```

   Provide input: `{"make": "Ford", "model": "Mustang", "price": 10000}`, press `ENTER` then end with `CTRL+C`

6. Consume the message from the topic:

  ```bash
  kafka-avro-console-consumer --bootstrap-server broker:29092 --from-beginning --topic cars --property schema.registry.url=http://localhost:8081 --group mygroup
  ```

7. What happens when you try to register schema version 3, which adds a new field "color"? Why could this version be incompatible?

  ```bash
  curl -w "\n" -X POST -H "Content-Type: application/vnd.schemaregistry.v1+json" \
  -d '{"schema": "{\"type\": \"record\", \"name\": \"Car\", \"namespace\": \"io.spoud.training\", \"fields\": [{\"name\": \"make\", \"type\": \"string\"}, {\"name\": \"model\", \"type\": \"string\"}, {\"name\": \"price\", \"type\": \"int\"}, {\"name\": \"color\", \"type\": \"string\"}]}"}' \
  http://localhost:8081/subjects/cars-value/versions
  ```

You can check compatibility by issuing the following command:

```bash
  curl -w "\n" -X POST -H "Content-Type: application/vnd.schemaregistry.v1+json" \
  -d '{"schema": "{\"type\": \"record\", \"name\": \"Car\", \"namespace\": \"io.spoud.training\", \"fields\": [{\"name\": \"make1\", \"type\": \"string\"}, {\"name\": \"model\", \"type\": \"string\"}, {\"name\": \"price\", \"type\": \"int\", \"default\": 0}]}"}' \
  http://localhost:8081/compatibility/subjects/cars-value/versions/latest?verbose=true
```


8. Change the compatibility mode for the subject to `FORWARD`, then try registering the v3 schema again (step 7).

  ```bash
  curl -w "\n" -X PUT -H "Content-Type: application/json" -d '{"compatibility": "FORWARD"}' \
  http://localhost:8081/config/cars-value
  ```

9. There should now be 3 versions available:

  ```bash
  curl -w "\n" http://localhost:8081/subjects/cars-value/versions
  ```

10. Can you read the v2 message, even when v3 is the latest version?

  ```bash
  kafka-avro-console-consumer --bootstrap-server broker:29092 --from-beginning --topic cars --property schema.registry.url=http://localhost:8081 --group mygroup2
  ```

What happened with the new field "color" in the v3 schema?

## Bonus: Send an avsc file to the schema registry

```bash
jq '. | {schema: tojson}' cars-value-v1.avsc | curl -w "\n" -X POST -H "Content-Type: application/vnd.schemaregistry.v1+json" -d @- http://localhost:8081/subjects/cars-value/versions
```



11. Delete subject permanently

  ```bash
  curl -w "\n" -X DELETE http://localhost:8081/subjects/cars-value
  curl -w "\n" -X DELETE http://localhost:8081/subjects/cars-value?permanent=true
  ```

12. From the broker container delete topic `cars`:

  ```bash
  docker-compose exec broker kafka-topics --bootstrap-server broker:29092 --delete --topic cars
  ```


## Bonus: schema compatibility groups

Try out the new `compatibilityGroup` feature.

<https://docs.confluent.io/platform/7.6/schema-registry/fundamentals/data-contracts.html#configuration-enhancements>

1. Follow steps 1 - 6 from above. Then:

2. Change the compatibility mode for the subject to FORWARD. We also set a `compatibilityGroup` so that compatibility is only checked for schemas that belong to the same major application version:

  ```bash
  curl -w "\n" -X PUT -H "Content-Type: application/json" -d '{"compatibility": "FORWARD", "compatibilityGroup": "application.major.version"}' http://localhost:8081/config/cars-value
  ```

3. then try registering the v3 schema again:

  ```bash
  curl -w "\n" -X POST -H "Content-Type: application/vnd.schemaregistry.v1+json" \
  -d '{"schema": "{\"type\": \"record\", \"name\": \"Car\", \"namespace\": \"io.spoud.training\", \"fields\": [{\"name\": \"make\", \"type\": \"string\"}, {\"name\": \"model\", \"type\": \"string\"}, {\"name\": \"price\", \"type\": \"int\"}, {\"name\": \"color\", \"type\": \"string\"}]}"}' \
  http://localhost:8081/subjects/cars-value/versions
  ```

4. There should now be 3 versions available:

  ```bash
  curl -w "\n" http://localhost:8081/subjects/cars-value/versions
  ```

5. Can you read the v2 message, even when v3 is the latest version?

  ```bash
  kafka-avro-console-consumer --bootstrap-server broker:29092 --from-beginning --topic cars --property schema.registry.url=http://localhost:8081
  ```

6. You can register an incompatible schema if you change the major application version

  ```bash
  curl -w "\n" -X POST -H "Content-Type: application/vnd.schemaregistry.v1+json" \
  -d '{"schema": "{\"type\": \"record\", \"name\": \"Car\", \"namespace\": \"io.spoud.training\", \"fields\": [{\"name\": \"manufacturer\", \"type\": \"string\"}, {\"name\": \"name\", \"type\": \"string\"}]}", "metadata": {"properties": {"application.major.version": "3"}}}' \
  http://localhost:8081/subjects/cars-value/versions
  ```
