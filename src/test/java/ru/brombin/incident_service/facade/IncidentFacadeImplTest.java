package ru.brombin.incident_service.facade;

import image.ImageServiceOuterClass.*;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.web.multipart.MultipartFile;
import ru.brombin.incident_service.dto.*;
import ru.brombin.incident_service.entity.*;
import ru.brombin.incident_service.mapper.ImageMapper;
import ru.brombin.incident_service.mapper.IncidentMapper;
import ru.brombin.incident_service.repository.IncidentRepository;
import ru.brombin.incident_service.security.JwtTokenProvider;
import ru.brombin.incident_service.service.IncidentServiceImpl;
import ru.brombin.incident_service.service.UserServiceImpl;
import ru.brombin.incident_service.service.grpc.GrpcClientServiceImpl;
import ru.brombin.incident_service.service.kafka.KafkaImageServiceImpl;
import ru.brombin.incident_service.util.exceptions.NotFoundException;

import java.util.Collections;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class IncidentFacadeImplTest {
    @Mock
    UserServiceImpl userService;
    @Mock
    JwtTokenProvider jwtTokenProvider;
    @Mock
    IncidentServiceImpl incidentService;
    @Mock
    ImageMapper imageMapper;
    @Mock
    IncidentMapper incidentMapper;
    @Mock
    GrpcClientServiceImpl grpcClientService;
    @Mock
    KafkaImageServiceImpl kafkaImageService;
    @Mock
    IncidentRepository incidentRepository;
    @InjectMocks
    IncidentFacadeImpl incidentFacade;


    @Test
    void createIncident_shouldCreateIncidentAndUploadImages() {
        // Arrange
        IncidentDto incidentDto = new IncidentDto(
                "Test Incident", "Test Description",
                null, null,
                IncidentStatus.OPEN, IncidentPriority.HIGH,
                IncidentCategory.SOFTWARE, ResponsibleService.IT_SUPPORT
        );
        MultipartFile image1 = mock(MultipartFile.class);
        MultipartFile image2 = mock(MultipartFile.class);
        List<MultipartFile> images = List.of(image1, image2);

        Incident incident = new Incident();
        incident.setId(1L);
        incident.setName("Test Incident"); // Убедитесь, что имя установлено
        when(userService.getCurrentUserId()).thenReturn(1L);
        when(incidentService.save(1L, incidentDto)).thenReturn(incident);

        ImageDto imageDto1 = new ImageDto(1L, "image1.jpg", 1024, "image/jpeg", new byte[]{1});
        ImageDto imageDto2 = new ImageDto(1L, "image2.jpg", 2048, "image/png", new byte[]{2});
        when(imageMapper.toImageDto(image1, incident.getId())).thenReturn(imageDto1);
        when(imageMapper.toImageDto(image2, incident.getId())).thenReturn(imageDto2);

        IncidentDto mappedIncidentDto = new IncidentDto(
                "Test Incident", "Test Description",
                null, null,
                IncidentStatus.OPEN, IncidentPriority.HIGH,
                IncidentCategory.SOFTWARE, ResponsibleService.IT_SUPPORT
        );
        when(incidentMapper.toDto(incident)).thenReturn(mappedIncidentDto);

        // Act
        IncidentDto result = incidentFacade.createIncident(incidentDto, images);

        // Assert
        verify(grpcClientService).uploadImages(List.of(imageDto1, imageDto2), incident);
        assertNotNull(result);
        assertEquals("Test Incident", result.name());
    }

    @Test
    void createIncident_withoutImages_shouldNotUploadImages() {
        // Arrange
        IncidentDto incidentDto = new IncidentDto(
                "Test Incident", "Test Description",
                null, null,
                IncidentStatus.OPEN, IncidentPriority.HIGH,
                IncidentCategory.SOFTWARE, ResponsibleService.IT_SUPPORT
        );

        Incident incident = new Incident();
        when(userService.getCurrentUserId()).thenReturn(1L);
        when(incidentService.save(1L, incidentDto)).thenReturn(incident);
        when(incidentMapper.toDto(incident)).thenReturn(incidentDto);

        // Act
        IncidentDto result = incidentFacade.createIncident(incidentDto, Collections.emptyList());

        // Assert
        verify(grpcClientService, never()).uploadImages(any(), any());
        assertNotNull(result);
    }


    @Test
    void findIncidentWithDetails_shouldReturnDetails() {
        // Arrange
        Incident incident = new Incident();
        incident.setId(1L);
        incident.setName("Test Incident");
        incident.setInitiatorId(1L);
        incident.setAnalystId(2L);

        when(incidentService.findById(1L)).thenReturn(incident);

        UserDto initiatorDto = new UserDto(
                "ROLE_USER", "User1",
                "user1", "user1@example.com",
                "123456789", "Office A"
        );

        UserDto analystDto = new UserDto(
                "ROLE_ANALYST", "User2",
                "user2", "user2@example.com",
                "987654321", "Office B"
        );

        when(userService.findById(1L)).thenReturn(Optional.of(initiatorDto));
        when(userService.findById(2L)).thenReturn(Optional.of(analystDto));

        ImageData imageData = ImageData.newBuilder()
                .setImageId(1L)
                .setFileName("image.jpg")
                .setSize(1024)
                .setType("image/jpeg")
                .build();

        when(grpcClientService.findImagesByIncidentId(1L)).thenReturn(List.of(imageData));
        when(imageMapper.toImageDtoList(List.of(imageData)))
                .thenReturn(List.of(new ImageDto(1L, "image.jpg", 1024, "image/jpeg", new byte[]{1})));

        when(incidentMapper.toDto(incident)).thenReturn(new IncidentDto("Test Incident", "Test Incident",
                null, 2L, IncidentStatus.OPEN, IncidentPriority.HIGH, IncidentCategory.SOFTWARE, ResponsibleService.IT_SUPPORT));
        when(incidentMapper.toIncidentWithDetailsDto(
                any(IncidentDto.class),
                anyList(),
                any(),
                any(UserDto.class)
        )).thenAnswer(invocation -> {
            IncidentDto dto = invocation.getArgument(0);
            List<ImageDto> images = invocation.getArgument(1);
            UserDto analyst = invocation.getArgument(2);
            UserDto initiator = invocation.getArgument(3);
            return new IncidentWithDetailsDto(dto, images, analyst, initiator);
        });

        // Act
        IncidentWithDetailsDto result = incidentFacade.findIncidentWithDetails(1L);

        // Assert intermediate results
        assertNotNull(incidentService.findById(1L), "IncidentService returned null");
        assertNotNull(userService.findById(1L).orElse(null), "Initiator UserDto is null");
        assertNotNull(userService.findById(2L).orElse(null), "Analyst UserDto is null");
        assertNotNull(grpcClientService.findImagesByIncidentId(1L), "GrpcClientService returned null images");
        assertNotNull(imageMapper.toImageDtoList(List.of(imageData)), "ImageMapper returned null");

        // Assert final result
        assertNotNull(result);
        assertEquals("Test Incident", result.incidentDto().name());
        assertEquals(1, result.imageDtos().size());
        assertEquals("User1", result.initiatorDto().fullName());
        assertEquals("User2", result.analystDto().fullName());
    }

    @Test
    void findIncidentWithDetails_shouldThrowNotFoundExceptionIfInitiatorNotFound() {
        // Arrange
        Incident incident = new Incident();
        incident.setId(1L);
        incident.setInitiatorId(1L);

        when(incidentService.findById(1L)).thenReturn(incident);
        when(userService.findById(1L)).thenReturn(Optional.empty());

        // Assert
        assertThrows(NotFoundException.class, () -> incidentFacade.findIncidentWithDetails(1L));
    }

    @Test
    void updateAnalyst_shouldUpdateAnalystSuccessfully() {
        // Arrange
        Long incidentId = 1L;
        Long analystId = 2L;
        IncidentDto updatedIncident = new IncidentDto(
                "Updated Incident", "Test Description",
                null, analystId,
                IncidentStatus.OPEN, IncidentPriority.HIGH,
                IncidentCategory.SOFTWARE, ResponsibleService.IT_SUPPORT
        );
        when(userService.findById(analystId)).thenReturn(Optional.of(new UserDto(
                "ROLE_ANALYST", "Analyst",
                "user", "analyst@example.com",
                "987654321", "Office B"
        )));
        when(incidentService.updateAnalyst(incidentId, analystId)).thenReturn(updatedIncident);

        // Act
        IncidentDto result = incidentFacade.updateAnalyst(incidentId, analystId);

        // Assert
        assertNotNull(result);
        assertEquals(analystId, result.analystId());
    }

    @Test
    void updateAnalyst_shouldThrowNotFoundExceptionIfAnalystNotFound() {
        // Arrange
        Long incidentId = 1L;
        Long analystId = 99L;

        when(userService.findById(analystId)).thenReturn(Optional.empty());

        // Assert
        assertThrows(NotFoundException.class, () -> incidentFacade.updateAnalyst(incidentId, analystId));
    }


    @Test
    void updateIncident_shouldUpdateIncidentForUserRole() {
        // Arrange
        Long incidentId = 1L;
        IncidentDto incidentDto = new IncidentDto(
                "Updated Incident", "Test Description",
                null, null,
                IncidentStatus.OPEN, IncidentPriority.HIGH,
                IncidentCategory.SOFTWARE, ResponsibleService.IT_SUPPORT
        );
        Incident existingIncident = new Incident();
        existingIncident.setName("Old Incident");
        existingIncident.setDescription("Old Description");

        when(userService.getCurrentUserRole()).thenReturn("ROLE_USER");
        when(incidentService.findById(incidentId)).thenReturn(existingIncident);

        Incident updatedIncident = new Incident();
        updatedIncident.setName("Updated Incident");
        updatedIncident.setDescription("Test Description");

        when(incidentRepository.save(existingIncident)).thenReturn(updatedIncident);
        when(incidentMapper.toDto(updatedIncident)).thenReturn(incidentDto);

        // Act
        IncidentDto result = incidentFacade.updateIncident(incidentId, incidentDto);

        // Assert
        assertEquals("Updated Incident", result.name());
        assertEquals("Test Description", result.description());
        verify(incidentRepository).save(existingIncident);
        verify(incidentService).findById(incidentId);
    }

    @Test
    void updateIncident_shouldUpdateIncidentForNonUserRole() {
        // Arrange
        Long incidentId = 1L;
        IncidentDto incidentDto = new IncidentDto(
                "Updated Incident", "Updated Description",
                null, null,
                IncidentStatus.OPEN, IncidentPriority.HIGH,
                IncidentCategory.SOFTWARE, ResponsibleService.IT_SUPPORT
        );

        Incident existingIncident = new Incident();
        when(userService.getCurrentUserRole()).thenReturn("ROLE_ADMIN");
        when(incidentService.findById(incidentId)).thenReturn(existingIncident);
        when(incidentService.update(incidentId, incidentDto)).thenReturn(incidentDto);

        // Act
        IncidentDto result = incidentFacade.updateIncident(incidentId, incidentDto);

        // Assert
        assertEquals("Updated Incident", result.name());
        assertEquals("Updated Description", result.description());
        verify(incidentService).update(incidentId, incidentDto);
        verify(incidentRepository, never()).save(any());
    }

    @Test
    void delete_shouldDeleteIncidentSuccessfully() {
        // Arrange
        Long incidentId = 1L;
        when(incidentService.findById(incidentId)).thenReturn(new Incident());
        when(jwtTokenProvider.extractJwtTokenValue()).thenReturn("mockedToken");

        // Act
        incidentFacade.delete(incidentId);

        // Assert
        verify(kafkaImageService).deleteImage(any(DeleteImageRequest.class), anyString());
        verify(incidentService).delete(incidentId);
    }

    @Test
    void delete_shouldThrowNotFoundExceptionIfIncidentNotFound() {
        // Arrange
        Long incidentId = 1L;
        when(incidentService.findById(incidentId)).thenThrow(new NotFoundException("Incident", incidentId));

        // Assert
        assertThrows(NotFoundException.class, () -> incidentFacade.delete(incidentId));
    }


}
