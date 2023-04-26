# clear all events from a topic

If we want to clear all the events from a topic we can set the retention time (`retention.ms`) to 0ms. This will delete all the events from the topic.

`kafka-configs --bootstrap-server localhost:9092 --entity-type topics --entity-name <topic> --alter --add-config retention.ms=0`

Try to consume from the topic.

`kafka-console-consumer --bootstrap-server localhost:9092 --topic <topic> --from-beginning`

You will see the following log messages:

```log
kafka-training-practice-broker-1  | [2023-04-26 08:39:21,073] INFO Processing override for entityPath: topics/java-batching-4 with config: HashMap(retention.ms -> 0) (kafka.server.ZkConfigManager)
kafka-training-practice-broker-1  | [2023-04-26 08:40:27,980] INFO [ProducerStateManager partition=java-batching-4-0] Wrote producer snapshot at offset 9874612 with 1 producer ids in 5 ms. (kafka.log.ProducerStateManager)
kafka-training-practice-broker-1  | [2023-04-26 08:40:27,982] INFO [MergedLog partition=java-batching-4-0, dir=/var/lib/kafka/data] Rolled new log segment at offset 9874612 in 14 ms. (kafka.log.MergedLog)
kafka-training-practice-broker-1  | [2023-04-26 08:40:27,985] INFO [MergedLog partition=java-batching-4-0, dir=/var/lib/kafka/data] Deleting segment LogSegment(baseOffset=0, size=50281550, lastModifiedTime=1682497853627, largestRecordTimestamp=Some(1682497853588)) due to retention time 0ms breach based on the largest record timestamp in the segment (kafka.log.MergedLog)
kafka-training-practice-broker-1  | [2023-04-26 08:40:27,994] INFO [MergedLog partition=java-batching-4-0, dir=/var/lib/kafka/data] Incrementing merged log start offset to 9874612 due to segment deletion (kafka.log.MergedLog)
kafka-training-practice-broker-1  | [2023-04-26 08:41:27,991] INFO [LocalLog partition=java-batching-4-0, dir=/var/lib/kafka/data] Deleting segment files LogSegment(baseOffset=0, size=50281550, lastModifiedTime=1682497853627, largestRecordTimestamp=Some(1682497853588)) (kafka.log.LocalLog$)
kafka-training-practice-broker-1  | [2023-04-26 08:41:28,002] INFO Deleted log /var/lib/kafka/data/java-batching-4-0/00000000000000000000.log.deleted. (kafka.log.LogSegment)
kafka-training-practice-broker-1  | [2023-04-26 08:41:28,004] INFO Deleted offset index /var/lib/kafka/data/java-batching-4-0/00000000000000000000.index.deleted. (kafka.log.LogSegment)
kafka-training-practice-broker-1  | [2023-04-26 08:41:28,004] INFO Deleted time index /var/lib/kafka/data/java-batching-4-0/00000000000000000000.timeindex.deleted. (kafka.log.LogSegment)
```

Because the newest message of the active segment is already older than the `retention.ms` the segment is rolled anyway.

Restore the retention.ms property to the default value
`kafka-configs --bootstrap-server localhost:9092 --entity-type topics --entity-name <topic> --alter --add-config retention.ms=604800000`