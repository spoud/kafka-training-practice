# Schema Registry API exercise solution

Use the exercise README for the full walkthrough:

- `../../kafka-exercises/6.1-schema-registry/README.md`

The validation path that was verified against the current `cp-schema-registry:8.2.0` image is:

1. List subjects:

   ```bash
   curl -w "\n" http://localhost:8081/subjects
   ```

2. Register schema version 1:

   ```bash
   curl -w "\n" -X POST -H "Content-Type: application/vnd.schemaregistry.v1+json" \
   -d '{"schema": "{\"type\": \"record\", \"name\": \"Car\", \"namespace\": \"io.spoud.training\", \"fields\": [{\"name\": \"make\", \"type\": \"string\"}, {\"name\": \"model\", \"type\": \"string\"}]}", "metadata": {"properties": {"application.major.version": "2"}}}' \
   http://localhost:8081/subjects/cars-value/versions
   ```

3. Register schema version 2 with `price` and verify the new version exists:

   ```bash
   curl -w "\n" -X POST -H "Content-Type: application/vnd.schemaregistry.v1+json" \
   -d '{"schema": "{\"type\": \"record\", \"name\": \"Car\", \"namespace\": \"io.spoud.training\", \"fields\": [{\"name\": \"make\", \"type\": \"string\"}, {\"name\": \"model\", \"type\": \"string\"}, {\"name\": \"price\", \"type\": \"int\", \"default\": 0}]}"}' \
   http://localhost:8081/subjects/cars-value/versions
   curl -w "\n" http://localhost:8081/subjects/cars-value/versions
   ```

4. Produce and consume an AVRO message:

   ```bash
   printf '{"make":"Ford","model":"Mustang","price":10000}\n' | \
   kafka-avro-console-producer --bootstrap-server broker:29092 \
     --property schema.registry.url=http://localhost:8081 \
     --topic cars \
     --property value.schema='{"type":"record","name":"Car","namespace":"io.spoud.training","fields":[{"name":"make","type":"string"},{"name":"model","type":"string"},{"name":"price","type":"int","default":0}]}'

   kafka-avro-console-consumer --bootstrap-server broker:29092 --from-beginning \
     --topic cars --property schema.registry.url=http://localhost:8081 --group mygroup
   ```

5. Registering version 3 with a new required `color` field is rejected under the default compatibility mode, but succeeds after switching the subject to `FORWARD`:

   ```bash
   curl -w "\n" -X PUT -H "Content-Type: application/json" -d '{"compatibility": "FORWARD"}' \
   http://localhost:8081/config/cars-value
   ```

6. Cleanup:

   ```bash
   curl -w "\n" -X DELETE http://localhost:8081/subjects/cars-value
   curl -w "\n" -X DELETE http://localhost:8081/subjects/cars-value?permanent=true
   docker compose exec broker kafka-topics --bootstrap-server broker:29092 --delete --topic cars
   ```
