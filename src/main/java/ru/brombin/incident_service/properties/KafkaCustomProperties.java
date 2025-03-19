package ru.brombin.incident_service.properties;

import lombok.AccessLevel;
import lombok.Data;
import lombok.experimental.FieldDefaults;
import org.apache.kafka.clients.producer.Producer;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.stereotype.Component;

@Data
@Component
@FieldDefaults(level = AccessLevel.PRIVATE)
@ConfigurationProperties(prefix = "spring.kafka")
public class KafkaCustomProperties {
    String bootstrapServers;
    Producer producer;

    @Data
    @FieldDefaults(level = AccessLevel.PRIVATE)
    public static class Producer {
        Integer retries;
        String acks;
        Integer deliveryTimeout;
        Integer retryBackoff;
        Integer lingerMs;
        Boolean enableIdempotence;
    }
}
