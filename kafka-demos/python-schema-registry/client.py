import argparse
from confluent_kafka import Producer, Consumer, KafkaException
from confluent_kafka.schema_registry import SchemaRegistryClient, header_schema_id_serializer
from confluent_kafka.schema_registry.avro import AvroSerializer, AvroDeserializer
from confluent_kafka.serialization import SerializationContext, MessageField

DEFAULT_SCHEMA_REGISTRY_URL = 'http://localhost:8081'
# For Confluent Cloud, add: 'basic.auth.user.info': '<sr-api-key>:<sr-api-secret>'
# See: https://docs.confluent.io/cloud/current/sr/index.html

USER_SCHEMA_STR = """
{
  "namespace": "com.example",
  "type": "record",
  "name": "User",
  "fields": [
    {"name": "name", "type": "string"},
    {"name": "age", "type": "int"}
  ]
}
"""

class User:
    def __init__(self, name, age):
        self.name = name
        self.age = age

    def to_dict(self):
        return {"name": self.name, "age": self.age}


def run_producer(topic, bootstrap_servers, schema_registry_url):
    schema_registry_client = SchemaRegistryClient({'url': schema_registry_url})
    avro_serializer = AvroSerializer(
        schema_registry_client,
        USER_SCHEMA_STR,
        lambda user, ctx: user.to_dict(),
        conf={"schema.id.serializer": header_schema_id_serializer, 'subject.name.strategy.type': 'TOPIC'}
    )
    producer = Producer({'bootstrap.servers': bootstrap_servers })

    print(f"Producing to topic '{topic}'. Enter 'name,age' per line. Press Ctrl+C to stop.")
    try:
        while True:
            line = input("> ").strip()
            if not line:
                continue
            parts = line.split(",", 1)
            if len(parts) != 2:
                print("Invalid input - expected format: name,age")
                continue
            name, raw_age = parts[0].strip(), parts[1].strip()
            try:
                age = int(raw_age)
            except ValueError:
                print(f"Invalid age '{raw_age}' - must be an integer")
                continue
            user = User(name=name, age=age)
            headers = []
            value = avro_serializer(user, SerializationContext(topic, MessageField.VALUE, headers=headers))
            producer.produce(topic, key=name, value=value, headers=headers)
            producer.flush()
            print(f"Produced: key={name} value={user.to_dict()}")
    except KeyboardInterrupt:
        print("\nStopped.")


def run_consumer(topic, bootstrap_servers, schema_registry_url):
    schema_registry_client = SchemaRegistryClient({'url': schema_registry_url})
    avro_deserializer = AvroDeserializer(
        schema_registry_client,
        USER_SCHEMA_STR,
        lambda obj, ctx: User(name=obj["name"], age=obj["age"]),
    )
    consumer = Consumer({
        'bootstrap.servers': bootstrap_servers,
        'group.id': 'python-schema-registry-demo',
        'auto.offset.reset': 'earliest',
    })
    consumer.subscribe([topic])
    print(f"Consuming from topic '{topic}'. Press Ctrl+C to stop.")

    try:
        while True:
            msg = consumer.poll(timeout=1.0)
            if msg is None:
                continue
            if msg.error():
                raise KafkaException(msg.error())
            headers = msg.headers() or []
            user = avro_deserializer(msg.value(), SerializationContext(topic, MessageField.VALUE, headers=headers))
            print(f"Consumed: key={msg.key().decode()} value={user.to_dict()}")
    except KeyboardInterrupt:
        print("\nStopped.")
    finally:
        consumer.close()


def main():
    parser = argparse.ArgumentParser(description="Avro Kafka client with Schema Registry")
    parser.add_argument(
        "--role",
        required=True,
        choices=["PRODUCER", "CONSUMER"],
        help="Run as PRODUCER or CONSUMER",
    )
    parser.add_argument(
        "--topic",
        default="users",
        help="Kafka topic (default: users)",
    )
    parser.add_argument(
        "--bootstrap-server",
        default="localhost:9092",
        dest="bootstrap_server",
        help="Kafka bootstrap server (default: localhost:9092)",
    )
    parser.add_argument(
        "--schema-registry",
        default=DEFAULT_SCHEMA_REGISTRY_URL,
        dest="schema_registry",
        help=f"Schema Registry URL (default: {DEFAULT_SCHEMA_REGISTRY_URL})",
    )

    args = parser.parse_args()

    if args.role == "PRODUCER":
        run_producer(args.topic, args.bootstrap_server, args.schema_registry)
    else:
        run_consumer(args.topic, args.bootstrap_server, args.schema_registry)


if __name__ == "__main__":
    main()