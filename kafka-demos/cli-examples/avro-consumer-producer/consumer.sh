kafka-avro-console-consumer --bootstrap-server localhost:9092 --from-beginning --topic avro-topic --property schema.registry.url=http://localhost:8081 --property "print.key=true"
