# Kafka Connect Demo

## Prepare Environment

1. start the connect-worker and its dependencies

```bash
      docker compose -f docker compose-elk.yml -f docker compose.yml up -d broker connect schema-registry elasticsearch kibana
```

2. install the plugins we are going to use

## Installing Connector Plugins

* The Datagen Source Connector is already present in our connect-worker

      curl localhost:8083/connector-plugins | jq

* we install the Elasticsearch connector to create an Elasticsearch sink

      docker compose exec connect bash
      confluent-hub install --no-prompt confluentinc/kafka-connect-elasticsearch:latest

* plugin is loaded after when the connect worker restarts

      curl localhost:8083/connector-plugins | jq
      docker compose restart connect

## Configuring Source and Sink Connectors


* Source connector

      curl -X PUT -H "Content-Type: application/json" --data @source-connector.json localhost:8083/connectors/source/config | jq

      curl localhost:8083/connectors/source/status | jq

* Check topic contents

      docker compose exec connect bash
      kafka-avro-console-consumer --bootstrap-server broker:29092 --topic purchases --from-beginning --property print.key=false --property schema.registry.url=http://schema-registry:8081

* Sink connector

      curl -X PUT -H "Content-Type: application/json" --data @sink-connector.json localhost:8083/connectors/sink/config | jq

      curl localhost:8083/connectors/sink/status | jq

* curl 'http://localhost:9200/purchases/_search?pretty'
* http://localhost:5601/app/home#/


## Delete a connector

      curl -X DELETE localhost:8083/connectors/sink

## Tear Down

`docker compose stop`


