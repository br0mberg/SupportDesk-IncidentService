package ru.brombin.incident_service.service.kafka;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.kafka.clients.producer.ProducerRecord;
import org.apache.kafka.common.header.Header;
import org.apache.kafka.common.header.internals.RecordHeader;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
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
public class KafkaImageServiceImpl implements KafkaImageService {

    private final KafkaSender<String, DeleteImageRequest> kafkaSender;

    @Value("${kafka.delete-image.topic}")
    String deleteImageTopic;

    private List<Header> createHeaders(String jwtToken) {
        return List.of(new RecordHeader("Authorization", jwtToken.getBytes(StandardCharsets.UTF_8)));
    }

    @Override
    public void deleteImage(DeleteImageRequest deleteImageRequest, String jwtToken) {
        List<Header> headers = createHeaders(jwtToken);
        SenderRecord<String, DeleteImageRequest, Object> senderRecord = SenderRecord.create(
                new ProducerRecord<>(deleteImageTopic, null, null, null, deleteImageRequest, headers), null
        );

        kafkaSender.send(Mono.just(senderRecord))
                .retryWhen(Retry.backoff(3, Duration.ofSeconds(1)))
                .subscribeOn(Schedulers.boundedElastic())
                .doOnError(e ->
                        log.error("Failed to send delete image request: {}", e.getMessage()))
                .subscribe(result ->
                        log.info("Delete image request sent successfully with offset: {}", result.recordMetadata().offset()));
    }
}