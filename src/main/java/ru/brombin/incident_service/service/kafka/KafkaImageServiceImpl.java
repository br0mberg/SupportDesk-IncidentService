package ru.brombin.incident_service.service.kafka;

import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import lombok.experimental.NonFinal;
import lombok.extern.slf4j.Slf4j;
import org.apache.kafka.clients.producer.ProducerRecord;
import org.apache.kafka.common.header.Header;
import org.apache.kafka.common.header.internals.RecordHeader;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.kafka.support.KafkaHeaders;
import org.springframework.messaging.Message;
import org.springframework.messaging.support.MessageBuilder;
import org.springframework.stereotype.Service;
import org.springframework.util.concurrent.ListenableFutureCallback;
import reactor.core.publisher.Mono;
import reactor.core.scheduler.Schedulers;
import reactor.kafka.sender.KafkaSender;
import reactor.kafka.sender.SenderRecord;
import reactor.util.retry.Retry;
import ru.brombin.incident_service.dto.DeleteImageRequest;

import java.nio.charset.StandardCharsets;
import java.time.Duration;
import java.util.List;

@Service
@Slf4j
@RequiredArgsConstructor
@FieldDefaults(level= AccessLevel.PRIVATE, makeFinal = true)
public class KafkaImageServiceImpl implements KafkaImageService {

    KafkaTemplate<String, DeleteImageRequest> kafkaTemplate;

    @NonFinal
    @Value("${kafka.delete-image.topic}")
    String deleteImageTopic;

    @Override
    public void deleteImage(DeleteImageRequest deleteImageRequest, String jwtToken) {
        Message<DeleteImageRequest> message = MessageBuilder
                .withPayload(deleteImageRequest)
                .setHeader(KafkaHeaders.TOPIC, deleteImageTopic)
                .setHeader(KafkaHeaders.KEY, "delete-image-key")
                .setHeader("Authorization", jwtToken)
                .build();

        kafkaTemplate.send(message)
                .whenComplete((result, ex) -> {
                    if (ex == null) {
                        log.info("Delete image request sent successfully with offset: {}",
                                result.getRecordMetadata().offset());
                    } else {
                        log.error("Failed to send delete image request: {}", ex.getMessage());
                    }
                });
    }
}