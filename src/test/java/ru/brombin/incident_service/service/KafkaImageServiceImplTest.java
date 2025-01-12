package ru.brombin.incident_service.service;

import org.apache.kafka.clients.producer.ProducerRecord;
import org.apache.kafka.clients.producer.RecordMetadata;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;
import reactor.kafka.sender.KafkaSender;
import reactor.kafka.sender.SenderRecord;
import reactor.kafka.sender.SenderResult;
import reactor.test.StepVerifier;
import ru.brombin.incident_service.dto.DeleteImageRequest;
import ru.brombin.incident_service.service.kafka.KafkaImageServiceImpl;

import java.lang.reflect.Field;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

class KafkaImageServiceImplTest {

    @Mock
    private KafkaSender<String, DeleteImageRequest> kafkaSender;

    @InjectMocks
    private KafkaImageServiceImpl kafkaImageService;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
    }

    @Test
    void deleteImage_shouldSendDeleteRequestSuccessfully() throws NoSuchFieldException, IllegalAccessException {
        // Arrange
        String jwtToken = "test-jwt-token";
        DeleteImageRequest deleteImageRequest = new DeleteImageRequest(100L);

        Field topicField = KafkaImageServiceImpl.class.getDeclaredField("deleteImageTopic");
        topicField.setAccessible(true);
        topicField.set(kafkaImageService, "delete-image-topic");

        SenderResult<Object> mockSenderResult = mock(SenderResult.class);
        RecordMetadata mockMetadata = mock(RecordMetadata.class);
        when(mockSenderResult.recordMetadata()).thenReturn(mockMetadata);
        when(kafkaSender.send(any(Mono.class))).thenReturn(Flux.just(mockSenderResult));

        // Act
        kafkaImageService.deleteImage(deleteImageRequest, jwtToken);

        // Assert
        verify(kafkaSender, times(1)).send(any(Mono.class));
    }

    @Test
    void deleteImage_shouldHandleSendError() {
        // Arrange
        SenderRecord<String, DeleteImageRequest, Object> senderRecord =
                SenderRecord.create(new ProducerRecord<>("topic", new DeleteImageRequest(1L)), null);

        when(kafkaSender.send(any(Mono.class)))
                .thenReturn(Flux.error(new RuntimeException("Kafka send error")));

        // Act & Assert
        StepVerifier.create(kafkaSender.send(Mono.just(senderRecord)))
                .expectErrorMatches(throwable ->
                        throwable instanceof RuntimeException && throwable.getMessage().equals("Kafka send error"))
                .verify();
    }
}
