package ru.brombin.incident_service.service.grpc;

import image.ImageServiceGrpc.*;
import image.ImageServiceOuterClass.*;
import io.grpc.StatusRuntimeException;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import ru.brombin.incident_service.dto.ImageDto;
import ru.brombin.incident_service.entity.Incident;
import ru.brombin.incident_service.mapper.ImageRequestMapper;
import ru.brombin.incident_service.service.JwtClientInterceptor;
import ru.brombin.incident_service.util.exceptions.ImageProcessingException;
import user.UserServiceGrpc.UserServiceBlockingStub;
import user.UserServiceOuterClass.UserResponse;
import user.UserServiceOuterClass.GetUserRequest;

import java.util.List;

import static lombok.AccessLevel.PRIVATE;

@Service
@Slf4j
@RequiredArgsConstructor
@FieldDefaults(level = PRIVATE, makeFinal = true)
public class GrpcClientServiceImpl implements GrpcClientService{

    ImageServiceBlockingStub imageServiceStub;
    UserServiceBlockingStub userServiceStub;
    ImageRequestMapper imageRequestMapper;

    @Override
    public void uploadImages(List<ImageDto> imageDtoList, Incident incident) {
        for (ImageDto imageDto : imageDtoList) {
            try {
                SaveImageRequest request = imageRequestMapper.toSaveImageRequest(imageDto);

                ImageServiceBlockingStub stubWithInterceptor = imageServiceStub.withInterceptors(new JwtClientInterceptor());

                SaveImageResponse response = stubWithInterceptor.saveImage(request);

                log.info("Image uploaded for Incident '{}', Image ID: {}, URL: {}",
                        incident.getId(), response.getImageId(), response.getUrl());
            } catch (StatusRuntimeException e) {
                log.error("gRPC error: Status code: {}, Details: {}, Cause: {}",
                        e.getStatus().getCode(), e.getStatus().getDescription(),
                        e.getCause() != null ? e.getCause().toString() : "null");
                throw new ImageProcessingException("Failed to upload image via gRPC for Incident: " + incident.getId(), e);
            } catch (Exception e) {
                log.error("Unexpected error occurred while uploading image for Incident '{}': {}",
                        incident.getId(), e.getMessage(), e);
                throw new ImageProcessingException("Failed to upload image via gRPC for Incident: "
                        + incident.getId() + " with unexpected error: ", e);
            }
        }
    }

    @Override
    public List<ImageData> findImagesByIncidentId(Long incidentId) {
            GetImagesRequest request = GetImagesRequest.newBuilder()
                    .setIncidentId(incidentId)
                    .build();

            try {
                ImageServiceBlockingStub stubWithInterceptor = imageServiceStub.withInterceptors(new JwtClientInterceptor());

                GetImagesResponse response = stubWithInterceptor.getImages(request);
                return response.getImagesList();

            } catch (StatusRuntimeException e) {
                log.error("gRPC StatusRuntimeException while fetching images for Incident '{}': Status code: {}, Details: {}, Cause: {}",
                        incidentId, e.getStatus().getCode(), e.getStatus().getDescription(),
                        e.getCause() != null ? e.getCause().toString() : "null");
                throw new ImageProcessingException("gRPC error fetching images for Incident: " + incidentId, e);
            } catch (Exception e) {
                log.error("Unexpected error while fetching images for Incident '{}': {}", incidentId, e.getMessage(), e);
                throw new ImageProcessingException("Unexpected error fetching images for Incident: " + incidentId, e);
            }
    }

    @Override
    public UserResponse findUserById(Long id) {
        GetUserRequest request = GetUserRequest.newBuilder()
                .setId(id)
                .build();

        try {
            UserServiceBlockingStub stubWithInterceptor = userServiceStub.withInterceptors(new JwtClientInterceptor());
            return stubWithInterceptor.getUserById(request);
        } catch (StatusRuntimeException e) {
            log.error("gRPC error fetching user by ID '{}': {}", id, e.getStatus().getDescription(), e);
            throw new RuntimeException("Failed to fetch user by ID via gRPC", e);
        } catch (Exception e) {
            log.error("Unexpected error fetching user by ID '{}': {}", id, e.getMessage(), e);
            throw new RuntimeException("Unexpected error fetching user by ID", e);
        }
    }
}
