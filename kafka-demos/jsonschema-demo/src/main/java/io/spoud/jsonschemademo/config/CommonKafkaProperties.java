package io.spoud.jsonschemademo.config;

import io.confluent.kafka.serializers.AbstractKafkaSchemaSerDeConfig;
import lombok.Getter;
import org.apache.kafka.clients.consumer.ConsumerConfig;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import java.util.Map;

@Getter
@Component
public class CommonKafkaProperties {
    @Value("${spring.kafka.consumer.auto-offset-reset:earliest}")
    private String autoOffsetReset;

    @Value("${kafka.bootstrap.servers}")
    private String bootstrapServers;
    @Value("${kafka.api.key}")
    private String apiKey;
    @Value("${kafka.api.secret}")
    private String apiSecret;
    @Value("${kafka.consumer.group.id}")
    private String consumerGroupId;

    @Value("${schema.registry.url}")
    private String schemaRegistryUrl;
    @Value("${schema.registry.api.key}")
    private String schemaRegistryApiKey;
    @Value("${schema.registry.api.secret}")
    private String schemaRegistryApiSecret;

    private final String JAAS_CONFIG_TEMPLATE = "org.apache.kafka.common.security.plain.PlainLoginModule required username='%s' password='%s';";

    public Map<String, Object> toPropertiesMap() {
        return Map.of(
            ConsumerConfig.AUTO_OFFSET_RESET_CONFIG, autoOffsetReset,
            "sasl.mechanism", "PLAIN",
            ConsumerConfig.BOOTSTRAP_SERVERS_CONFIG, bootstrapServers,
            "sasl.jaas.config", JAAS_CONFIG_TEMPLATE.formatted(apiKey, apiSecret),
            "security.protocol", "SASL_SSL",
            ConsumerConfig.GROUP_ID_CONFIG, consumerGroupId,
            AbstractKafkaSchemaSerDeConfig.SCHEMA_REGISTRY_URL_CONFIG, schemaRegistryUrl,
            AbstractKafkaSchemaSerDeConfig.BASIC_AUTH_CREDENTIALS_SOURCE, "USER_INFO",
            AbstractKafkaSchemaSerDeConfig.AUTO_REGISTER_SCHEMAS, "false",
            AbstractKafkaSchemaSerDeConfig.USER_INFO_CONFIG, "%s:%s".formatted(schemaRegistryApiKey, schemaRegistryApiSecret)
        );
    }
}
