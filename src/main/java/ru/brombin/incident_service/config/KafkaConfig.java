package ru.brombin.incident_service.config;

import lombok.AccessLevel;
import lombok.experimental.FieldDefaults;
import org.apache.kafka.clients.producer.ProducerConfig;
import org.apache.kafka.common.serialization.StringSerializer;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.kafka.core.DefaultKafkaProducerFactory;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.kafka.core.ProducerFactory;
import org.springframework.kafka.support.serializer.JsonSerializer;
import ru.brombin.incident_service.dto.DeleteImageRequest;

import java.util.HashMap;
import java.util.Map;

@Configuration
@FieldDefaults(level= AccessLevel.PRIVATE)
public class KafkaConfig {

    @Value("${spring.kafka.bootstrap-servers}")
    String bootstrapServers;

    @Value("${spring.kafka.producer.retries}")
    Integer retries;

    @Value("${spring.kafka.producer.properties.enable.idempotence}")
    Boolean enableIdempotence;

    @Value("${spring.kafka.producer.acks}")
    String acks;

    @Value("${spring.kafka.producer.delivery-timeout}")
    Integer deliveryTimeout;

    @Value("${spring.kafka.producer.retry-backoff}")
    Integer retryBackoff;

    @Value("${spring.kafka.producer.linger-ms}")
    Integer lingerMs;

    @Bean
    public KafkaTemplate<String, DeleteImageRequest> kafkaTemplate() {
        return new KafkaTemplate<>(producerFactory());
    }

    private ProducerFactory<String, DeleteImageRequest> producerFactory() {
        return new DefaultKafkaProducerFactory<>(producerConfigs(), new StringSerializer(), new JsonSerializer<>());
    }

    @Bean
    public Map<String, Object> producerConfigs() {
        Map<String, Object> props = new HashMap<>();
        props.put(ProducerConfig.BOOTSTRAP_SERVERS_CONFIG, bootstrapServers);
        props.put(ProducerConfig.KEY_SERIALIZER_CLASS_CONFIG, StringSerializer.class);
        props.put(ProducerConfig.VALUE_SERIALIZER_CLASS_CONFIG, JsonSerializer.class);
        props.put(ProducerConfig.RETRIES_CONFIG, retries);
        props.put(ProducerConfig.ACKS_CONFIG, acks);
        props.put(ProducerConfig.DELIVERY_TIMEOUT_MS_CONFIG, deliveryTimeout);
        props.put(ProducerConfig.RETRY_BACKOFF_MS_CONFIG, retryBackoff);
        props.put(ProducerConfig.LINGER_MS_CONFIG, lingerMs);
        props.put(ProducerConfig.ENABLE_IDEMPOTENCE_CONFIG, enableIdempotence);

        return props;
    }
}