
**Careful with the kafka ports**

When you connect from within the broker you can use `localhost:9092`.

When you connect from your host you can use `localhost:9092`.

When you connect form another container you need to use `broker:29092`.


# Exercise 2.1 - CLI

* Start your docker-compose environment
* Check that the broker is running with the control center on http://localhost:9021

There is a `kcat` container waiting for you to be used.

    docker-compose exec kcat sh

Within the container run `kcat` commands like the following listing the metadata:

    kcat -b broker:29092 -L    # on the kcat container
    kcat -b localhost:9092 -L  # on your local machine

You can run kafka client cli within the broker container:

    docker-compose exec broker bash

List the topic with the kafka client cli:

    kafka-topics --bootstrap-server localhost:9092 --list | grep -v "^_"  # topics without system topics


## Exercise 1 - Create a topic

Create a topic named `topic-3p` with 3 partition and a replication factor of 1.

Solution:

    kafka-topics --bootstrap-server localhost:9092 --create --topic topic-3p --partitions 3 --replication-factor 1

    kafka-topics --bootstrap-server localhost:9092 --describe --topic topic-3p

Result:
    Topic: topic-3p TopicId: wqAfso3iQHOemAFVBBZyZQ PartitionCount: 3       ReplicationFactor: 1    Configs:
    Topic: topic-3p Partition: 0    Leader: 1       Replicas: 1     Isr: 1  Offline:
    Topic: topic-3p Partition: 1    Leader: 1       Replicas: 1     Isr: 1  Offline:
    Topic: topic-3p Partition: 2    Leader: 1       Replicas: 1     Isr: 1  Offline:


## Exercise 2 - Produce messages into this topic

Produce messages into the topic `topic-3p` with the following content:

    {"name": "foo", "value": 0}
    {"name": "bar", "value": 1}
    {"name": "baz", "value": 2}


Solution:

    echo '{"name": "foo", "value": 0}' | kcat -b broker:29092 -t topic-3p -P
    echo '{"name": "bar", "value": 1}' | kcat -b broker:29092 -t topic-3p -P
    echo '{"name": "baz", "value": 2}' | kcat -b broker:29092 -t topic-3p -P

Solution with kafka-command-line-client:

    kafka-console-producer --broker-list localhost:9092 --topic topic-3p <<< '{"name": "foo", "value": 0}'
    kafka-console-producer --broker-list localhost:9092 --topic topic-3p <<< '{"name": "bar", "value": 1}'
    kafka-console-producer --broker-list localhost:9092 --topic topic-3p <<< '{"name": "baz", "value": 2}'

## Exercise 3 - Consume messages from this topic

Consume messages from the topic `topic-3p` with the following content:

    {"name": "foo", "value": 0}
    {"name": "bar", "value": 1}
    {"name": "baz", "value": 2}


Solution:

    kcat -b broker:29092 -t topic-3p -K: -C

Solution with kafka-command-line-client:

    kafka-console-consumer --bootstrap-server localhost:9092 --topic topic-3p --from-beginning

## Exercise 4 - Delete the topic

Delete the topic `topic-3p`.

Solution:

    kafka-topics --bootstrap-server localhost:9092 --delete --topic topic-3p

    kafka-topics --bootstrap-server localhost:9092 --list | grep topic-3p

