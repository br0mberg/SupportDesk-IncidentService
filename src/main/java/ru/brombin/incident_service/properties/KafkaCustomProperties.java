package ru.brombin.incident_service.properties;

import lombok.AccessLevel;
import lombok.Data;
import lombok.experimental.FieldDefaults;
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
        int retries;
        String acks;
        int deliveryTimeout;
        int retryBackoff;
        int lingerMs;
        boolean enableIdempotence;
    }
}
