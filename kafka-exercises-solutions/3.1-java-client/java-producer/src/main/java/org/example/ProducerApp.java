package org.example;

import java.util.Properties;
import org.apache.kafka.clients.producer.KafkaProducer;
import org.apache.kafka.clients.producer.Producer;
import org.apache.kafka.clients.producer.ProducerRecord;

/**
 * Simple java producer
 *
 */
public class ProducerApp {
  public static void main(String[] args) {
    Properties props = getDefaultProps();

    produce(props);
  }

  static void produce(Properties props) {
    try (Producer<String, String> producer = new KafkaProducer<>(props)) {
      for (int i = 0; i < 100; i++) {
        producer.send(
            new ProducerRecord<>("hello-from-java", Integer.toString(i), Integer.toString(i)));
        Thread.sleep(10);
      }
    } catch (Exception e) {
      e.printStackTrace();
    }
  }

  static Properties getDefaultProps() {
    Properties props = new Properties();
    props.put("bootstrap.servers", "localhost:9092");
    props.put("acks", "all");
    props.put("key.serializer", "org.apache.kafka.common.serialization.StringSerializer");
    props.put("value.serializer", "org.apache.kafka.common.serialization.StringSerializer");
    props.put("linger.ms", 100);
    return props;
  }
}
