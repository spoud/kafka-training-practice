package io.spoud.training.flink;

import org.apache.flink.api.common.eventtime.WatermarkStrategy;
import org.apache.flink.configuration.Configuration;
import org.apache.flink.configuration.CoreOptions;
import org.apache.flink.connector.kafka.source.KafkaSource;
import org.apache.flink.connector.kafka.source.KafkaSourceBuilder;
import org.apache.flink.connector.kafka.source.enumerator.initializer.OffsetsInitializer;
import org.apache.flink.formats.avro.registry.confluent.ConfluentRegistryAvroDeserializationSchema;
import org.apache.flink.streaming.api.datastream.DataStreamSource;
import org.apache.flink.streaming.api.datastream.SingleOutputStreamOperator;
import org.apache.flink.streaming.api.environment.StreamExecutionEnvironment;

import io.spoud.training.flink.Orders;
import org.apache.flink.streaming.api.windowing.assigners.TumblingProcessingTimeWindows;

import java.time.Duration;
import java.util.HashMap;
import java.util.Map;

public class DataStreamDemo {

    public static void main(String[] args) throws Exception {
        final StreamExecutionEnvironment env;
        if (args.length > 0 && args[0].equals("local")) {
            Configuration conf = new Configuration();
            conf.set(CoreOptions.DEFAULT_PARALLELISM, 1);
            env = StreamExecutionEnvironment.createLocalEnvironmentWithWebUI(conf);
        } else {
            env = StreamExecutionEnvironment.getExecutionEnvironment();
        }

        KafkaSourceBuilder<Orders> builder = KafkaSource.builder();
        builder.setBootstrapServers("localhost:9092");
        builder.setTopics("orders");
        builder.setGroupId("flink-test");
        Map<String, String> srProperties = new HashMap<>();
        /* for Schema Registry with basic auth:
         srProperties.put("basic.auth.credentials.source", "USER_INFO");
         srProperties.put("basic.auth.user.info", "user:password");
         */
        builder.setValueOnlyDeserializer(ConfluentRegistryAvroDeserializationSchema.forSpecific(Orders.class, "http://localhost:8081", srProperties));

        /* For confluent cloud:
        builder.setProperty("security.protocol", "SASL_SSL")
                .setProperty("sasl.mechanism", "PLAIN")
                .setProperty("sasl.jaas.config", "org.apache.kafka.common.security.plain.PlainLoginModule required username=\"THE_ONE\" password=\"RED_PILL:-)\";");
         */

        builder.setStartingOffsets(OffsetsInitializer.earliest());
        KafkaSource<Orders> source = builder
                .build();

        DataStreamSource<Orders> ordersDataStreamSource = env.fromSource(source, WatermarkStrategy.forBoundedOutOfOrderness(Duration.ofSeconds(10)), "Kafka Source");

        ordersDataStreamSource.print();

        SingleOutputStreamOperator<String> aggregated = ordersDataStreamSource
                .keyBy(Orders::getProductid)
                .window(TumblingProcessingTimeWindows.of(Duration.ofSeconds(60)))
                .reduce((o1, o2) -> {
                    o1.setOrderunits(o1.getOrderunits() + o2.getOrderunits());
                    return o1;
                })
                .map(o -> o.getProductid() + " -> " + o.getOrderunits());
        aggregated.print();

        env.execute("Flink DataStream Demo");
    }
}
