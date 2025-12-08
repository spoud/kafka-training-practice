package io.spoud.training;

import org.apache.kafka.clients.producer.internals.BuiltInPartitioner;
import org.apache.kafka.common.serialization.IntegerSerializer;
import org.apache.kafka.common.serialization.StringSerializer;

import java.util.HashMap;
import java.util.Map;

public class KeyspaceDemo
{
    public static void main( String[] args )
    {
        int numPartitions = 6;
        int keySpace = 13;
        Map<Integer, Integer> keyPerPartition = new HashMap<>();

        for (int i = 0; i < 100; i++) {
            // experiment with strings
            String key = "stream-" + (i % keySpace);
            byte[] keyBytes = new StringSerializer().serialize(null, key);
            int partition = BuiltInPartitioner.partitionForKey(keyBytes, numPartitions);
/*          // experiment with ints
            int key = (i % keySpace);
            byte[] keyBytes = new IntegerSerializer().serialize(null, key);
            int partition = BuiltInPartitioner.partitionForKey(keyBytes, numPartitions);
*/

            if (keyPerPartition.containsKey(partition)) {
                keyPerPartition.put(partition, keyPerPartition.get(partition) + 1);
            } else {
                keyPerPartition.put(partition, 1);
            }
            System.out.println("key: " + key + " goes to partition " + partition);
        }

        System.out.println("\n\nKeyspace " + keySpace + "\nKeys per Partition: ");

        for (int i = 0; i < numPartitions; i++) {
            System.out.println("Partition " + i + ": " +
                    "█".repeat(keyPerPartition.getOrDefault(i, 0)) + " (" + keyPerPartition.get(i) + ")");
        }
    }
}
