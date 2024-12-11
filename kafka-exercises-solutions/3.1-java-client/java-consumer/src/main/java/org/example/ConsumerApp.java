package org.example;

import java.time.Duration;
import java.util.Collection;
import java.util.List;
import java.util.Map;
import java.util.Properties;
import java.util.concurrent.ConcurrentHashMap;
import org.apache.kafka.clients.consumer.Consumer;
import org.apache.kafka.clients.consumer.ConsumerRebalanceListener;
import org.apache.kafka.clients.consumer.KafkaConsumer;
import org.apache.kafka.common.TopicPartition;

public class ConsumerApp {
  // Last message per partition and per key
  private static final Map<Integer, Map<String, String>> MEMORY = new ConcurrentHashMap<>();

  public static void main(String[] args) {

    Properties props = new Properties();
    props.put("bootstrap.servers", "localhost:9092");
    props.put("group.id", "test1");
    props.put("key.deserializer", "org.apache.kafka.common.serialization.StringDeserializer");
    props.put("value.deserializer", "org.apache.kafka.common.serialization.StringDeserializer");
    props.put("auto.offset.reset", "earliest");

    ConsumerRebalanceListener rebalanceListener = new ConsumerRebalanceListener() {
      @Override
      public void onPartitionsRevoked(Collection<TopicPartition> partitions) {
        partitions.forEach(partition -> {
          MEMORY.remove(partition.partition());
        });
      }

      @Override
      public void onPartitionsAssigned(Collection<TopicPartition> partitions) {
        partitions.forEach(partition -> {
          MEMORY.put(partition.partition(), new ConcurrentHashMap<>());
        });
      }
    };

    try (Consumer<String, String> consumer = new KafkaConsumer<>(props)) {
      consumer.subscribe(List.of("hello-from-java"), rebalanceListener);
      while (true) {
        consumer.poll(Duration.ofSeconds(1)).forEach(record -> {
          MEMORY.get(record.partition()).put(record.key(), record.value());

          System.out.println(record.key() + " " + record.value());

          printMemory();
        });
      }
    } catch (Exception e) {
      e.printStackTrace();
    }

    System.out.println("The End!");
  }

  public static void printMemory() {
    System.out.println("=======  Memory =========");
    System.out.println("Memory: ");
    MEMORY.forEach((partition, map) -> {
      System.out.println("Partition: " + partition);
      map.forEach((key, value) -> {
        System.out.println("\t Key: " + key + " Value: " + value);
      });
    });
    System.out.println("========================");
  }
}
