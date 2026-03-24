package io.spoud.kafka_standby;

import org.apache.kafka.clients.consumer.Consumer;
import org.apache.kafka.clients.consumer.ConsumerRebalanceListener;
import org.apache.kafka.clients.producer.Producer;
import org.apache.kafka.common.TopicPartition;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.kafka.autoconfigure.KafkaProperties;
import org.springframework.context.Lifecycle;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.kafka.config.ConcurrentKafkaListenerContainerFactory;
import org.springframework.kafka.config.KafkaListenerEndpointRegistry;
import org.springframework.kafka.core.ConsumerFactory;
import org.springframework.kafka.core.DefaultKafkaConsumerFactory;
import org.springframework.kafka.core.DefaultKafkaProducerFactory;
import org.springframework.kafka.core.ProducerFactory;
import org.springframework.kafka.listener.ConsumerAwareRebalanceListener;
import org.springframework.scheduling.annotation.EnableScheduling;
import org.springframework.scheduling.annotation.Scheduled;

import java.util.Collection;
import java.util.Map;

@Configuration
@EnableScheduling
public class KafkaConfig {

    private static final Logger log = LoggerFactory.getLogger(KafkaConfig.class);

    @Autowired
    KafkaProperties kafkaProperties;

    @Autowired
    FeatureFlagService featureFlagService;

    @Autowired
    KafkaListenerEndpointRegistry registry;

    @Bean
    public ConsumerFactory<String, String> consumerFactory() {
        return new DefaultKafkaConsumerFactory<>(kafkaProperties.buildConsumerProperties());
    }

    @Bean
    public ProducerFactory<?, ?> producerFactory() {
        return new StandbyAwareProducerFactory<>(kafkaProperties.buildProducerProperties(), featureFlagService);
    }

    @Bean
    public ConcurrentKafkaListenerContainerFactory<String, String> kafkaListenerContainerFactory() {
        ConcurrentKafkaListenerContainerFactory<String, String> factory = new ConcurrentKafkaListenerContainerFactory<>(
        );
        factory.setConsumerFactory(consumerFactory());
        factory.getContainerProperties().setConsumerRebalanceListener(new ConsumerAwareRebalanceListener() {
            @Override
            public void onPartitionsRevokedAfterCommit(Consumer<?, ?> consumer, Collection<TopicPartition> partitions) {
                log.info("Revoked partitions: {}", partitions);
            }

            @Override
            public void onPartitionsAssigned(Consumer<?, ?> consumer, Collection<TopicPartition> partitions) {
                log.info("Assigned partitions: {}", partitions);
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

    public static class StandbyAwareProducerFactory<K, V> extends DefaultKafkaProducerFactory<K, V> {
        private final FeatureFlagService featureFlagService;

        public StandbyAwareProducerFactory(Map<String, Object> config, FeatureFlagService featureFlagService) {
            super(config);
            this.featureFlagService = featureFlagService;
        }

        @Override
        protected Producer<K, V> createRawProducer(Map<String, Object> rawConfigs) {
            var producer = super.createRawProducer(rawConfigs);
            return new StandbyAwareProducer<>(producer, featureFlagService);
        }
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
