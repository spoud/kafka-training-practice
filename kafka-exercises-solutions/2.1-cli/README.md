# Exercise 2.1 - Kafka CLI

**Note:** Please be careful with the Kafka ports

- When you connect from within the broker you can use `localhost:9092`.
- When you connect from your host you can use `localhost:9092`.
- When you connect from another container you need to use `broker:29092`.

## Setup

- Start your docker-compose environment with the start script `./start.sh`
- Check that the broker is running with the control center on <http://localhost:9021>

There is a `kcat` container waiting for you to be used.

```bash
docker-compose -f docker-compose.yml -f docker-compose-kcat.yml exec kcat sh
```

Within the container run `kcat` commands like the following to list the metadata:

```bash
kcat -b broker:29092 -L    # on the kcat container
kcat -b localhost:9092 -L  # on your local machine
```

You can run the Kafka client CLI within the broker container:

```bash
docker-compose exec broker bash
```

List the topic with the Kafka client CLI:

```bash
kafka-topics --bootstrap-server localhost:9092 --list | grep -v "^_"  # Filter out system topics starting with `_`
```

Within the broker type `kafka-`-TAB-TAB to see all available commands within the Confluent Kafka distribution.

In the following exercises, you will use the Kafka client CLI to create, produce, consume and delete topics.
Watch out for the right CLI tools in the list.

## Exercise 1 - Create a topic

Create a topic named `topic-3p` with 3 partitions and a replication factor of 1.

### Solution

```bash
kafka-topics --bootstrap-server localhost:9092 --create --topic topic-3p --partitions 3 --replication-factor 1
kafka-topics --bootstrap-server localhost:9092 --describe --topic topic-3p
```

### Result

```log
Topic: topic-3p TopicId: wqAfso3iQHOemAFVBBZyZQ PartitionCount: 3       ReplicationFactor: 1    Configs:
Topic: topic-3p Partition: 0    Leader: 1       Replicas: 1     Isr: 1  Offline:
Topic: topic-3p Partition: 1    Leader: 1       Replicas: 1     Isr: 1  Offline:
Topic: topic-3p Partition: 2    Leader: 1       Replicas: 1     Isr: 1  Offline:
```

## Exercise 2 - Produce messages into this topic

Produce messages into the topic `topic-3p` with the following content:

```json
{"name": "foo", "value": 0}
{"name": "bar", "value": 1}
{"name": "baz", "value": 2}
```

### Solution

```bash
echo '{"name": "foo", "value": 0}' | kcat -b broker:29092 -t topic-3p -P
echo '{"name": "bar", "value": 1}' | kcat -b broker:29092 -t topic-3p -P
echo '{"name": "baz", "value": 2}' | kcat -b broker:29092 -t topic-3p -P
```

### Solution with Kafka CLI client

```bash
kafka-console-producer --bootstrap-server localhost:9092 --topic topic-3p <<< '{"name": "foo", "value": 0}'
kafka-console-producer --bootstrap-server localhost:9092 --topic topic-3p <<< '{"name": "bar", "value": 1}'
kafka-console-producer --bootstrap-server localhost:9092 --topic topic-3p <<< '{"name": "baz", "value": 2}'
```

## Exercise 3 - Consume messages from this topic

Consume messages from the topic `topic-3p` with the following content:

```json
{"name": "foo", "value": 0}
{"name": "bar", "value": 1}
{"name": "baz", "value": 2}
```

### Solution

```bash
kcat -b broker:29092 -t topic-3p -K: -C
```

### Solution with Kafka CLI client

```bash
kafka-console-consumer --bootstrap-server localhost:9092 --topic topic-3p --from-beginning
```

## Exercise 4 - Delete the topic

Delete the topic `topic-3p`.

### Solution

```bash
kafka-topics --bootstrap-server localhost:9092 --delete --topic topic-3p
kafka-topics --bootstrap-server localhost:9092 --list | grep topic-3p
```

Try again to consume from the topic and note the error message.

### Solution

```log
Error while fetching metadata with correlation id 2 : {topic-3p=UNKNOWN_TOPIC_OR_PARTITION} (org.apache.kafka.clients.NetworkClient)
```

## Exercise 5 - Create a compacted topic `scoreboard` with 3 partitions and the following settings

- `cleanup.policy=compact`
- `min.cleanable.dirty.ratio=0.01`
- `segment.ms=60000`

**Caution** The `segment.ms` setting is bad for production since it will create a new segment file every minute.

### Solution

```bash
kafka-topics --bootstrap-server localhost:9092 --create --topic scoreboard --partitions 3 --replication-factor 1 \
--config cleanup.policy=compact --config min.cleanable.dirty.ratio=0.01 --config segment.ms=60000 
```

## Write scoreboard messages to the topic

Here is a small script to produce some scoreboard messages

```bash
declare -A s=([p1]=0 [p2]=0 [p3]=0 [p4]=0 [p5]=0 [p6]=0 [p7]=0 [p8]=0 [p9]=0);
for i in {1..1000}; do
  id="p$(( ($i-1) % 9 + 1 ))"
  inc=$((RANDOM%10+1))
  s[$id]=$((s[$id]+inc))
  echo -e "$id\t{\"player_id\":\"$id\",\"score\":${s[$id]}}"
done
```

### Solution

```bash
declare -A s=([p1]=0 [p2]=0 [p3]=0 [p4]=0 [p5]=0 [p6]=0 [p7]=0 [p8]=0 [p9]=0);
for i in {1..1000}; do
  id="p$(( ($i-1) % 9 + 1 ))"
  inc=$((RANDOM%10+1))
  s[$id]=$((s[$id]+inc))
  echo -e "$id\t{\"player_id\":\"$id\",\"score\":${s[$id]}}"
done | kafka-console-producer --bootstrap-server localhost:9092 --topic scoreboard --property parse.key=true
```

## Read all data from the scoreboard topic

Read all data from the scoreboard topic and print the message partition, offset, key and value.

What result do you expect?

### Solution

```bash
kafka-console-consumer --bootstrap-server localhost:9092 --topic scoreboard --from-beginning --property print.key=true \
--property print.offset=true --property print.partition=true
```

## Fulfill the requirements for the scoreboard topic to be compacted

You will see that the topic is not compacted yet. To fulfill the requirements for the topic to be compacted you need to make sure there is a non-active segment for each partition.

Deep dive article: <https://strimzi.io/blog/2021/12/17/kafka-segment-retention/>

### Solution

To see the compaction in action you need to add another message after one minute to each partition (use the following keys `p0`, `p1`, `p4`).

```bash
sleep 60; 
echo -e "p0\ttrigger-compaction" | kafka-console-producer --bootstrap-server localhost:9092 --topic scoreboard --property parse.key=true
echo -e "p1\ttrigger-compaction" | kafka-console-producer --bootstrap-server localhost:9092 --topic scoreboard --property parse.key=true
echo -e "p4\ttrigger-compaction" | kafka-console-producer --bootstrap-server localhost:9092 --topic scoreboard --property parse.key=true
```

## Read again all data from the scoreboard topic

Read all data from the scoreboard topic and print the message partition, offset, key and value.

### Solution

```bash
kafka-console-consumer --bootstrap-server localhost:9092 --topic scoreboard --from-beginning --property print.key=true \
--property print.offset=true --property print.partition=true
```
