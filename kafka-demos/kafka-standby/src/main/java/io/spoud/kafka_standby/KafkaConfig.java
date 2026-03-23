package io.spoud.kafka_standby;

import org.apache.kafka.clients.consumer.ConsumerConfig;
import org.apache.kafka.clients.consumer.ConsumerRebalanceListener;
import org.apache.kafka.common.TopicPartition;
import org.apache.kafka.common.serialization.StringDeserializer;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.Lifecycle;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.kafka.config.ConcurrentKafkaListenerContainerFactory;
import org.springframework.kafka.config.KafkaListenerEndpointRegistry;
import org.springframework.kafka.core.ConsumerFactory;
import org.springframework.kafka.core.DefaultKafkaConsumerFactory;
import org.springframework.kafka.listener.MessageListenerContainer;
import org.springframework.scheduling.annotation.EnableScheduling;
import org.springframework.scheduling.annotation.Scheduled;

import java.util.Collection;
import java.util.HashMap;
import java.util.Map;

@Configuration
@EnableScheduling
public class KafkaConfig {

    private static final Logger log = LoggerFactory.getLogger(KafkaConfig.class);
    @Value("${spring.kafka.bootstrap-servers}")
    private String bootstrapServers;

    @Value("${spring.kafka.consumer.group-id}")
    private String groupId;

    @Autowired
    FeatureFlagService featureFlagService;

    @Autowired
    KafkaListenerEndpointRegistry registry;

    @Bean
    public ConsumerFactory<String, String> consumerFactory() {
        Map<String, Object> props = new HashMap<>();
        props.put(ConsumerConfig.BOOTSTRAP_SERVERS_CONFIG, bootstrapServers);
        props.put(ConsumerConfig.GROUP_ID_CONFIG, groupId);
        props.put(ConsumerConfig.KEY_DESERIALIZER_CLASS_CONFIG, StringDeserializer.class);
        props.put(ConsumerConfig.VALUE_DESERIALIZER_CLASS_CONFIG, StringDeserializer.class);
        props.put(ConsumerConfig.AUTO_OFFSET_RESET_CONFIG, "earliest");
        props.put(ConsumerConfig.MAX_POLL_INTERVAL_MS_CONFIG, 15_000);
        return new DefaultKafkaConsumerFactory<>(props);
    }

    @Bean
    public ConcurrentKafkaListenerContainerFactory<String, String> kafkaListenerContainerFactory() {
        ConcurrentKafkaListenerContainerFactory<String, String> factory = new ConcurrentKafkaListenerContainerFactory<>(
        );
        factory.setConsumerFactory(consumerFactory());
        factory.getContainerProperties().setConsumerRebalanceListener(new ConsumerRebalanceListener() {
            @Override
            public void onPartitionsRevoked(Collection<TopicPartition> partitions) {
                log.info("Consumer rebalance: partitions revoked: {}", partitions);
            }

            @Override
            public void onPartitionsAssigned(Collection<TopicPartition> partitions) {
                log.info("Consumer rebalance: partitions assigned: {}", partitions);
            }
        });
        factory.setContainerCustomizer(container -> {
            if (featureFlagService.isStandbyModeEnabled()) {
                log.info("Pausing Kafka consumer at startup: {}", container.getListenerId());
                container.stop();
            } else {
                log.info("This application is not in standby mode. Will run Kafka consumer: {}", container.getListenerId());
            }
        });
        return factory;
    }

    @Scheduled(fixedRate = 5000)
    public void reactToStandbyState() {
        if (featureFlagService.isStandbyModeEnabled()) {
            registry.getAllListenerContainers()
                    .stream()
                    .filter(Lifecycle::isRunning)
                    .forEach(container -> {
                        log.info("Pausing Kafka consumer: {}", container.getListenerId());
                        container.stop();
                    });
        } else {
            registry.getAllListenerContainers()
                    .stream()
                    .filter(container -> !container.isRunning())
                    .forEach(container -> {
                        log.info("Resuming Kafka consumer: {}", container.getListenerId());
                        container.start();
                    });
        }
    }
}
