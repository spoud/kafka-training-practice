# Schema Registry API Exercise

* Confluent Schema Registry provides a RESTful API for managing schemas and subjects
* Confluent Schema Registry documentation: https://docs.confluent.io/platform/current/schema-registry/index.html 
* API documentation: https://docs.confluent.io/platform/current/schema-registry/develop/api.html


## Preparation

* Start the required docker compose services with `docker-compose up -d zookeeper broker schema-registry`
* **Note**: If you don't have curl installed on your PC, then you can also execute these commands inside the schema-registry container, but you'll need to replace the bootstrap url with `broker:9092`:
`docker-compose exec schema-registry bash`



## Exercise - Schema Registry API

1. Display all currently available subjects in the Schema Registry  

       curl http://localhost:8081/subjects

2. We want to register a new schema for record values in topic `cars`

       curl -X POST -H "Content-Type: application/vnd.schemaregistry.v1+json" \
        -d '{"schema": "{\"type\": \"record\", \"name\": \"Car\", \"namespace\": \"io.spoud.training\", \"fields\": [{\"name\": \"make\", \"type\": \"string\"}, {\"name\": \"model\", \"type\": \"string\"}]}"}' \
        http://localhost:8081/subjects/cars-value/versions

3. Display the registered schema with `curl http://localhost:8081/subjects/cars-value/versions/1/schema`

4. Version 2 of our schema contains a new field "price". Register this new version.

       curl -X POST -H "Content-Type: application/vnd.schemaregistry.v1+json" \
       -d '{"schema": "{\"type\": \"record\", \"name\": \"Car\", \"namespace\": \"io.spoud.training\", \"fields\": [{\"name\": \"make\", \"type\": \"string\"}, {\"name\": \"model\", \"type\": \"string\"}, {\"name\": \"price\", \"type\": \"int\", \"default\": 0}]}"}' \
       http://localhost:8081/subjects/cars-value/versions

5. Produce a message to the topic with this schema:

       kafka-avro-console-producer --bootstrap-server broker:29092 --property schema.registry.url=http://localhost:8081 --topic cars \
       --property value.schema='{"type": "record", "name": "Car", "namespace": "io.spoud.training", "fields": [{"name": "make", "type": "string"}, {"name": "model", "type": "string"}, {"name": "price", "type": "int", "default":  0}]}'

   Provide input: `{"make": "Ford", "model": "Mustang", "price": 10000}`, press `ENTER` then end with `CTRL+C`

6. What happens when you try to register schema version 3, which adds a new field "color"? Why could this version be incompatible?

       curl -X POST -H "Content-Type: application/vnd.schemaregistry.v1+json" \
       -d '{"schema": "{\"type\": \"record\", \"name\": \"Car\", \"namespace\": \"io.spoud.training\", \"fields\": [{\"name\": \"make\", \"type\": \"string\"}, {\"name\": \"model\", \"type\": \"string\"}, {\"name\": \"price\", \"type\": \"int\"}, {\"name\": \"color\", \"type\": \"string\"}]}"}' \
       http://localhost:8081/subjects/cars-value/versions

7. Change the compatibility mode for the subject to FORWARD, then try registering the v3 schema again. 

       curl -X PUT -H "Content-Type: application/json" -d '{"compatibility": "FORWARD"}' \
       http://localhost:8081/config/cars-value

8. There should now be 3 versions available:

       curl http://localhost:8081/subjects/cars-value/versions

9. Can you read the v2 message, even when v3 is the latest version?

       kafka-avro-console-consumer --bootstrap-server broker:29092 --from-beginning --topic cars --property schema.registry.url=http://localhost:8081
