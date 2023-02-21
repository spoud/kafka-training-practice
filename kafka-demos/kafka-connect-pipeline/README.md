# Kafka Connect Demo

## Prepare Environment

1. start the connect-worker and its dependencies  
   `docker-compose up zookeeper broker connect schema-registry`
2. install the plugins we are going to use  
3. `docker-compose exec `

## Installing Connector Plugins

* The Datagen Source Connector is already present in our connect-worker  
  `curl localhost:8083/connector-plugins`
* we install the JDBC connector to create a JDBC sink
  
      docker-compose exec connect bash
      confluent-hub install confluentinc/kafka-connect-jdbc:10.6.3
* plugin is loaded after when the connect worker restarts 

      curl localhost:8083/connector-plugins
      docker-compose restart connect

## Configuring Source and Sink Connectors



## Tear Down

docker-compose stop


