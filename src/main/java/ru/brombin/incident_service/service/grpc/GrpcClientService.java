package ru.brombin.incident_service.service.grpc;

import image.ImageServiceOuterClass.ImageData;
import ru.brombin.incident_service.dto.ImageDto;
import ru.brombin.incident_service.entity.Incident;
import user.UserServiceOuterClass.UserResponse;

import java.util.List;

public interface GrpcClientService {
    void uploadImages(List<ImageDto> imageDtoList, Incident incident);
    List<ImageData> findImagesByIncidentId(Long incidentId);
    UserResponse findUserById(Long id);
}