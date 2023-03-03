
**Careful with the kafka ports**

When you connect from within the broker you can use `localhost:9092`.

When you connect from your host you can use `localhost:9092`.

When you connect form another container you need to use `broker:29092`.


# Exercise 2.1 - CLI

* Start your docker-compose environment with the start script `./start.sh`
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

Within the broker type `kafka-`-TAB-TAB to see all available commands within the confluent kafka distribution.

In the following exercises you will use the kafka client cli to create, produce, consume and delete topics.
Watch out for the right cli tools in the list.

## Exercise 1 - Create a topic

Create a topic named `topic-3p` with 3 partition and a replication factor of 1.

Solution:

    ...

## Exercise 2 - Produce messages into this topic

Produce messages into the topic `topic-3p` with the following content:

    {"name": "foo", "value": 0}
    {"name": "bar", "value": 1}
    {"name": "baz", "value": 2}


Solution:

    ...

Solution with kafka-command-line-client:

    ...

## Exercise 3 - Consume messages from this topic

Consume messages from the topic `topic-3p` with the following content:

    {"name": "foo", "value": 0}
    {"name": "bar", "value": 1}
    {"name": "baz", "value": 2}


Solution:

   ...

Solution with kafka-command-line-client:

    ...

## Exercise 4 - Delete the topic

Delete the topic `topic-3p`.

Solution:

    ...


Try again to consume from the topic and note the error message.

Solution:

    ...



## Exercise 5 - Create a compacted topic `scoreboard` with 3 partitions and the following settings

* `cleanup.policy=compact`
* `min.cleanable.dirty.ratio=0.01`
* `segment.ms=60000`

**Caution** The segment.ms setting is bad for production sine it will create a new segment every minute.

Solution:

    ...


## Write scoreboard messages to the topic 

Here is a small script to produce some scoreboard messages

```bash
declare -A s=([p1]=0 [p2]=0 [p3]=0 [p4]=0 [p5]=0 [p6]=0 [p7]=0 [p8]=0 [p9]=0);
for i in {1..1000}; do id="p$(( ($i-1) % 9 + 1 ))"; inc=$((RANDOM%10+1)); s[$id]=$((s[$id]+inc)); echo -e "{\"player_id\":\"$id\",\"score\":${s[$id]}}"; done
```


Solution:

    ...

## Read all data from the scoreboard topic

Read all data from the scoreboard topic and print the message partition, offset, key and value.

What result do you expect?

Solution:

    ...

## Fulfill the requirements for the scoreboard topic to be compacted

You will see that the topic is not compacted yet. To fulfill the requirements for the topic to be compacted you need to make sure there is a non-active segment for each partition.

Deep dive article https://strimzi.io/blog/2021/12/17/kafka-segment-retention/

Solution:
    
    ...

## Read all data from the scoreboard topic

Read all data from the scoreboard topic and print the message partition, offset, key and value.

Solution:

    ...
