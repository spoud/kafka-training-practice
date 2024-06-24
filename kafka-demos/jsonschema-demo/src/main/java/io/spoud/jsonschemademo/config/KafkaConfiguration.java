package io.spoud.jsonschemademo.config;

import io.confluent.kafka.serializers.subject.TopicRecordNameStrategy;
import io.spoud.jsonschemademo.model.MediaEvent;
import io.spoud.jsonschemademo.model.MovieModel;
import io.confluent.kafka.serializers.KafkaJsonDeserializerConfig;
import io.confluent.kafka.serializers.json.KafkaJsonSchemaDeserializer;
import io.confluent.kafka.serializers.json.KafkaJsonSchemaSerializer;
import io.confluent.kafka.serializers.json.KafkaJsonSchemaSerializerConfig;
import org.apache.kafka.clients.producer.ProducerConfig;
import org.apache.kafka.common.serialization.StringDeserializer;
import org.apache.kafka.common.serialization.StringSerializer;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.kafka.config.ConcurrentKafkaListenerContainerFactory;
import org.springframework.kafka.core.*;
import org.springframework.kafka.listener.ContainerProperties;

import java.util.HashMap;
import java.util.Map;

@Configuration
public class KafkaConfiguration {
    public static final String MEDIA_TOPIC_NAME = "${kafka.topic.movie}";

    @Bean
    public ProducerFactory<String, MediaEvent> mediaEventProducerFactory(CommonKafkaProperties props) {
        Map<String, Object> config = new HashMap<>(props.toPropertiesMap());
        // use JSONSchema serializer
        config.put(ProducerConfig.KEY_SERIALIZER_CLASS_CONFIG, StringSerializer.class.getCanonicalName());
        config.put(ProducerConfig.VALUE_SERIALIZER_CLASS_CONFIG, KafkaJsonSchemaSerializer.class.getCanonicalName());
        config.put(KafkaJsonSchemaSerializerConfig.AUTO_REGISTER_SCHEMAS, true);
        config.put(KafkaJsonSchemaSerializerConfig.VALUE_SUBJECT_NAME_STRATEGY, TopicRecordNameStrategy.class.getCanonicalName());
        ProducerFactory<String, MediaEvent> factory = new DefaultKafkaProducerFactory<>(config);
        return factory;
    }

    @Bean
    public KafkaTemplate<String, MediaEvent> mediaEventKafkaTemplate(ProducerFactory<String, MediaEvent> factory) {
        return new KafkaTemplate<>(factory);
    }

    @Bean
    public ConsumerFactory<String, MediaEvent> mediaConsumerFactory(CommonKafkaProperties props) {
        var configs = new HashMap<>(props.toPropertiesMap());
        configs.put(KafkaJsonDeserializerConfig.JSON_VALUE_TYPE, MediaEvent.class.getCanonicalName());
        var deserializer = new KafkaJsonSchemaDeserializer<MediaEvent>();
        deserializer.configure(configs, false);
        return new DefaultKafkaConsumerFactory<>(
            configs,
            new StringDeserializer(),
            deserializer
        );
    }

    @Bean
    public ConcurrentKafkaListenerContainerFactory<String, MediaEvent> mediaListenerContainerFactory(ConsumerFactory<String, MediaEvent> consumerFactory) {
        ConcurrentKafkaListenerContainerFactory<String, MediaEvent> factory = new ConcurrentKafkaListenerContainerFactory<>();
        factory.setConsumerFactory(consumerFactory);
        // WARNING: we use MANUAL ack mode here because we want to control when offsets are committed. If you set this in your code, make sure you don't forget to commit offsets.
        // In this case, we actually don't want to commit offsets at all, because we want to replay the messages every time the application starts.
        factory.setContainerCustomizer(container -> container.getContainerProperties().setAckMode(ContainerProperties.AckMode.MANUAL));
        return factory;
    }
}
