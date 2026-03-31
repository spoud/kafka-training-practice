package io.spoud.kafka_standby;

import org.apache.kafka.clients.consumer.ConsumerGroupMetadata;
import org.apache.kafka.clients.consumer.OffsetAndMetadata;
import org.apache.kafka.clients.producer.Callback;
import org.apache.kafka.clients.producer.Producer;
import org.apache.kafka.clients.producer.ProducerRecord;
import org.apache.kafka.clients.producer.RecordMetadata;
import org.apache.kafka.common.*;
import org.apache.kafka.common.errors.ProducerFencedException;
import org.apache.kafka.common.metrics.KafkaMetric;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.time.Duration;
import java.util.List;
import java.util.Map;
import java.util.concurrent.Future;

public class StandbyAwareProducer <K,V> implements Producer<K,V> {
    public static class ProducerOnStandbyException extends RuntimeException {
        public ProducerOnStandbyException() {
            super("This application is in standby mode. Cannot send messages or perform transactions.");
        }
    }

    private static final Logger log = LoggerFactory.getLogger(StandbyAwareProducer.class);
    private final Producer<K,V> wrappedProducer;
    private final FeatureFlagService featureFlagService;

    public StandbyAwareProducer(Producer<K,V> wrappedProducer, FeatureFlagService featureFlagService) {
        this.wrappedProducer = wrappedProducer;
        this.featureFlagService = featureFlagService;
    }

    @Override
    public void initTransactions() {
        wrappedProducer.initTransactions();
    }

    @Override
    public void beginTransaction() throws ProducerFencedException {
        // we do not allow starting new transactions when in standby mode
        if (featureFlagService.isStandbyModeEnabled()) {
            throw new ProducerOnStandbyException();
        } else {
            wrappedProducer.beginTransaction();
        }
    }

    @Override
    public void sendOffsetsToTransaction(Map<TopicPartition, OffsetAndMetadata> map, ConsumerGroupMetadata consumerGroupMetadata) throws ProducerFencedException {
        wrappedProducer.sendOffsetsToTransaction(map, consumerGroupMetadata);
    }

    @Override
    public void commitTransaction() throws ProducerFencedException {
        // we allow committing ongoing transactions
        wrappedProducer.commitTransaction();
    }

    @Override
    public void abortTransaction() throws ProducerFencedException {
        wrappedProducer.abortTransaction();
    }

    @Override
    public void registerMetricForSubscription(KafkaMetric kafkaMetric) {
        wrappedProducer.registerMetricForSubscription(kafkaMetric);
    }

    @Override
    public void unregisterMetricFromSubscription(KafkaMetric kafkaMetric) {
        wrappedProducer.unregisterMetricFromSubscription(kafkaMetric);
    }

    @Override
    public Future<RecordMetadata> send(ProducerRecord<K, V> producerRecord) {
        if (featureFlagService.isStandbyModeEnabled()) {
            throw new ProducerOnStandbyException();
        }
        return wrappedProducer.send(producerRecord);
    }

    @Override
    public Future<RecordMetadata> send(ProducerRecord<K, V> producerRecord, Callback callback) {
        if (featureFlagService.isStandbyModeEnabled()) {
            throw new ProducerOnStandbyException();
        }
        return wrappedProducer.send(producerRecord, callback);
    }

    @Override
    public void flush() {
        // we allow flushing outstanding messages even when in standby mode
        wrappedProducer.flush();
    }

    @Override
    public List<PartitionInfo> partitionsFor(String s) {
        return wrappedProducer.partitionsFor(s);
    }

    @Override
    public Map<MetricName, ? extends Metric> metrics() {
        return wrappedProducer.metrics();
    }

    @Override
    public Uuid clientInstanceId(Duration duration) {
        return wrappedProducer.clientInstanceId(duration);
    }

    @Override
    public void close() {
        wrappedProducer.close();
    }

    @Override
    public void close(Duration duration) {
        wrappedProducer.close(duration);
    }
}
