package org.acme;

import io.smallrye.common.annotation.Identifier;
import io.smallrye.reactive.messaging.kafka.KafkaConsumerRebalanceListener;
import org.apache.kafka.clients.consumer.Consumer;
import org.apache.kafka.common.TopicPartition;
import org.jboss.logging.Logger;

import jakarta.enterprise.context.ApplicationScoped;
import jakarta.inject.Inject;
import java.util.Arrays;
import java.util.Collection;

@ApplicationScoped
@Identifier("rebalancer")
public class KafkaRebalanceListener implements KafkaConsumerRebalanceListener {

    @Inject
    StateStore stateStore;

    private static final Logger LOGGER = Logger.getLogger(KafkaRebalanceListener.class.getName());

    @Override
    public void onPartitionsAssigned(Consumer<?, ?> consumer, Collection<TopicPartition> partitions) {
        LOGGER.info("Partitions assigned: " + Arrays.toString(partitions.toArray()));
        KafkaConsumerRebalanceListener.super.onPartitionsAssigned(consumer, partitions);

        for (TopicPartition partition : partitions) {
            LOGGER.info("Assigned " + partition);
            consumer.seek(partition, Math.max(consumer.position(partition) - 10, 0));
        }
    }

    @Override
    public void onPartitionsLost(Consumer<?, ?> consumer, Collection<TopicPartition> partitions) {
        LOGGER.info("Partitions lost: " + Arrays.toString(partitions.toArray()));
        KafkaConsumerRebalanceListener.super.onPartitionsLost(consumer, partitions);
        partitions.forEach(partition -> stateStore.removePartition(partition.partition()));
    }

    @Override
    public void onPartitionsRevoked(Consumer<?, ?> consumer, Collection<TopicPartition> partitions) {
        LOGGER.info("Partitions revoked: " + Arrays.toString(partitions.toArray()));
        KafkaConsumerRebalanceListener.super.onPartitionsRevoked(consumer, partitions);
        partitions.forEach(partition -> stateStore.removePartition(partition.partition()));
    }
}
