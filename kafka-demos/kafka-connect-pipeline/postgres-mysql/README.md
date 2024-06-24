# Kafka Connect Demo - Postres -> MySQL

## Prepare the environment

Start the connect-worker and its dependencies

```bash
docker compose up -d
docker compose exec mysql bash
mysql --user root --password=debezium < /setup/mysql-setup.sql
```

## Configure connectors

Inspect the source database

```bash
docker compose exec postgres psql -U postgres -c 'SELECT * FROM inventory.customers;'
```

Deploy the source connector

```bash
curl -X PUT -H "Content-Type: application/json" --data @source-connector.json localhost:8083/connectors/postgres-source/config | jq
curl localhost:8083/connectors/postgres-source/status | jq
docker compose logs connect
```

Inspect the generated topics

```bash
docker compose exec broker kafka-topics --bootstrap-server localhost:9092 --list
docker compose exec broker kafka-console-consumer --bootstrap-server localhost:9092 --topic inventory-source.inventory.customers --from-beginning --property print.key=false --max-messages 4 | jq
```

Deploy the sink connector

```bash
curl -X PUT -H "Content-Type: application/json" --data @sink-connector.json localhost:8083/connectors/mysql-sink/config | jq
curl localhost:8083/connectors/mysql-sink/status | jq
docker compose logs connect
```

Check the output
```bash
docker compose exec mysql mysql --user root --password=debezium -e 'SELECT * FROM `inventory-source_inventory_customers`;' copy
```

## Trigger change

Make a change to the source

```bash
docker compose exec postgres psql -U postgres -c "UPDATE inventory.customers SET email='change@email.com' WHERE id=1003;"
docker compose exec postgres psql -U postgres -c 'SELECT * FROM inventory.customers;'
```

Inspect changes in the target database

```bash
docker compose exec mysql mysql --user root --password=debezium -e 'SELECT * FROM `inventory-source_inventory_customers`;' copy
```

## Inspect offsets

```bash
curl localhost:8083/connectors/postgres-source/offsets | jq
```

The offsets of a source connector return data specific for the source system. In this case, the log sequence number (LSN) denotes the last position that the connector commited within Postgres.

```bash
curl localhost:8083/connectors/mysql-sink/offsets | jq
```

The offsets of a target connector are regular Kafka offsets.

## Clean up

Delete a connector

```bash
curl -X DELETE localhost:8083/connectors/postgres-source
curl -X DELETE localhost:8083/connectors/mysql-sink
```

Tear Down

```bash
docker compose down
```
