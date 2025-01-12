package ru.brombin.incident_service.service;

import image.ImageServiceGrpc.*;
import image.ImageServiceOuterClass.*;
import io.grpc.StatusRuntimeException;
import lombok.AccessLevel;
import lombok.experimental.FieldDefaults;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import ru.brombin.incident_service.dto.ImageDto;
import ru.brombin.incident_service.entity.Incident;
import ru.brombin.incident_service.mapper.ImageRequestMapper;
import ru.brombin.incident_service.service.grpc.GrpcClientServiceImpl;
import ru.brombin.incident_service.util.exceptions.ImageProcessingException;
import user.UserServiceGrpc.*;
import user.UserServiceOuterClass.*;

import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@FieldDefaults(level= AccessLevel.PRIVATE)
class GrpcClientServiceImplTest {
    @Mock
    ImageServiceBlockingStub imageServiceStub;

    @Mock
    UserServiceBlockingStub userServiceStub;

    @Mock
    ImageRequestMapper imageRequestMapper;

    @InjectMocks
    GrpcClientServiceImpl grpcClientService;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
    }

    @Test
    void uploadImages_shouldUploadAllImagesSuccessfully() {
        // Arrange
        Incident incident = new Incident();
        incident.setId(123L);
        List<ImageDto> imageDtoList = List.of(
                new ImageDto(incident.getId(), "image1.jpg", 1024L, "image/jpeg", new byte[]{1, 2, 3}),
                new ImageDto(incident.getId(), "image2.jpg", 2048L, "image/png", new byte[]{4, 5, 6})
        );


        SaveImageRequest mockRequest = SaveImageRequest.newBuilder().build();
        SaveImageResponse mockResponse = SaveImageResponse.newBuilder()
                .setImageId(1L)
                .setUrl("http://example.com/image.jpg")
                .build();

        when(imageRequestMapper.toSaveImageRequest(any(ImageDto.class))).thenReturn(mockRequest);
        when(imageServiceStub.withInterceptors(any())).thenReturn(imageServiceStub);
        when(imageServiceStub.saveImage(any(SaveImageRequest.class))).thenReturn(mockResponse);

        // Act
        grpcClientService.uploadImages(imageDtoList, incident);

        // Assert
        verify(imageServiceStub, times(2)).saveImage(mockRequest);
    }

    @Test
    void uploadImages_shouldThrowImageProcessingExceptionOnGrpcError() {
        // Arrange
        Incident incident = new Incident();
        incident.setId(123L);

        List<ImageDto> imageDtoList = List.of(
                new ImageDto(incident.getId(), "image1.jpg", 1024L, "image/jpeg", new byte[]{1, 2, 3})
        );

        SaveImageRequest mockRequest = SaveImageRequest.newBuilder().build();

        // Настройка моков
        when(imageRequestMapper.toSaveImageRequest(any(ImageDto.class))).thenReturn(mockRequest);
        when(imageServiceStub.withInterceptors(any())).thenReturn(imageServiceStub);
        when(imageServiceStub.saveImage(any(SaveImageRequest.class)))
                .thenThrow(new StatusRuntimeException(io.grpc.Status.INTERNAL));

        // Act & Assert
        assertThrows(ImageProcessingException.class, () -> grpcClientService.uploadImages(imageDtoList, incident));
        verify(imageServiceStub, times(1)).saveImage(mockRequest);
    }

    @Test
    void findImagesByIncidentId_shouldReturnImages() {
        // Arrange
        Long incidentId = 123L;

        GetImagesRequest mockRequest = GetImagesRequest.newBuilder().setIncidentId(incidentId).build();
        GetImagesResponse mockResponse = GetImagesResponse.newBuilder()
                .addImages(ImageData.newBuilder().setImageId(1L).build())
                .addImages(ImageData.newBuilder().setImageId(2L).build())
                .build();

        when(imageServiceStub.withInterceptors(any())).thenReturn(imageServiceStub);
        when(imageServiceStub.getImages(mockRequest)).thenReturn(mockResponse);

        // Act
        List<ImageData> images = grpcClientService.findImagesByIncidentId(incidentId);

        // Assert
        assertEquals(2, images.size());
        verify(imageServiceStub, times(1)).getImages(mockRequest);
    }

    @Test
    void findImagesByIncidentId_shouldThrowImageProcessingExceptionOnGrpcError() {
        // Arrange
        Long incidentId = 123L;

        GetImagesRequest mockRequest = GetImagesRequest.newBuilder().setIncidentId(incidentId).build();

        when(imageServiceStub.withInterceptors(any())).thenReturn(imageServiceStub);
        when(imageServiceStub.getImages(mockRequest))
                .thenThrow(new StatusRuntimeException(io.grpc.Status.INTERNAL));

        // Act & Assert
        assertThrows(ImageProcessingException.class, () -> grpcClientService.findImagesByIncidentId(incidentId));
        verify(imageServiceStub, times(1)).getImages(mockRequest);
    }

    @Test
    void findUserById_shouldReturnUserResponse() {
        // Arrange
        Long userId = 123L;

        GetUserRequest mockRequest = GetUserRequest.newBuilder().setId(userId).build();
        UserResponse mockResponse = UserResponse.newBuilder()
                .setId(userId)
                .setFullName("Test User")
                .build();

        when(userServiceStub.withInterceptors(any())).thenReturn(userServiceStub);
        when(userServiceStub.getUserById(mockRequest)).thenReturn(mockResponse);

        // Act
        UserResponse response = grpcClientService.findUserById(userId);

        // Assert
        assertEquals(userId, response.getId());
        assertEquals("Test User", response.getFullName());
        verify(userServiceStub, times(1)).getUserById(mockRequest);
    }

    @Test
    void findUserById_shouldThrowRuntimeExceptionOnGrpcError() {
        // Arrange
        Long userId = 123L;

        GetUserRequest mockRequest = GetUserRequest.newBuilder().setId(userId).build();

        when(userServiceStub.withInterceptors(any())).thenReturn(userServiceStub);
        when(userServiceStub.getUserById(mockRequest))
                .thenThrow(new StatusRuntimeException(io.grpc.Status.INTERNAL));

        // Act & Assert
        assertThrows(RuntimeException.class, () -> grpcClientService.findUserById(userId));
        verify(userServiceStub, times(1)).getUserById(mockRequest);
    }
}
