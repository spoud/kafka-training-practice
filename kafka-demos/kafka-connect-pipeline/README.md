# Kafka Connect Demo

## Prepare Environment

1. start the connect-worker and its dependencies  
   `docker-compose up -d zookeeper broker connect schema-registry`
   `docker-compose -f docker-compose-elk.yml up -d`
2. install the plugins we are going to use  

## Installing Connector Plugins

* The Datagen Source Connector is already present in our connect-worker  
  `curl localhost:8083/connector-plugins | jq`
* we install the JDBC connector to create a JDBC sink
  
      docker-compose exec connect bash
      confluent-hub install confluentinc/kafka-connect-elasticsearch:latest
 
* plugin is loaded after when the connect worker restarts 

      curl localhost:8083/connector-plugins | jq
      docker-compose restart connect

## Configuring Source and Sink Connectors


* Source connector

      curl -X PUT -H "Content-Type: application/json" --data @source-connector.json localhost:8083/connectors/source/config | jq

      curl localhost:8083/connectors/source/status | jq

* Check topic contents

      kafka-avro-console-consumer --bootstrap-server localhost:9092 --topic purchases --from-beginning --property print.key=false

* Sink connector

      curl -X PUT -H "Content-Type: application/json" --data @sink-connector.json localhost:8083/connectors/sink/config | jq

      curl localhost:8083/connectors/sink/status | jq

* curl 'http://localhost:9200/es-sink/_search?pretty'
* http://localhost:5601/app/home#/
    

## Delete a connector

      curl -X DELETE localhost:8083/connectors/sink

## Tear Down

`docker-compose stop`


