package ru.brombin.incident_service.service.kafka;

import ru.brombin.incident_service.dto.DeleteImageRequest;

public interface KafkaImageService {
    void deleteImage(DeleteImageRequest deleteImageRequest, String jwtToken);
}
