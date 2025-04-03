package ru.brombin.incident_service.service;

import lombok.AccessLevel;
import lombok.experimental.FieldDefaults;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.dao.DataAccessException;
import org.springframework.dao.EmptyResultDataAccessException;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;
import ru.brombin.incident_service.builder.IncidentSpecificationBuilder;
import ru.brombin.incident_service.dto.IncidentDto;
import ru.brombin.incident_service.dto.IncidentFilterDto;
import ru.brombin.incident_service.entity.*;
import ru.brombin.incident_service.mapper.IncidentMapper;
import ru.brombin.incident_service.repository.IncidentRepository;
import ru.brombin.incident_service.util.exceptions.NotFoundException;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.Mockito.*;

/*
@FieldDefaults(level= AccessLevel.PRIVATE)
class IncidentServiceImplTest {
    @Mock
    IncidentRepository incidentRepository;
    @Mock
    IncidentSpecificationBuilder specificationBuilder;
    @Mock
    IncidentMapper incidentMapper;
    @InjectMocks
    IncidentServiceImpl incidentService;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
    }

    @Test
    void findById_shouldReturnIncident_whenIdExists() {
        // Arrange
        Long incidentId = 1L;
        Incident mockIncident = new Incident();
        mockIncident.setId(incidentId);
        when(incidentRepository.findById(incidentId)).thenReturn(Optional.of(mockIncident));

        // Act
        Incident result = incidentService.findById(incidentId);

        // Assert
        assertThat(result).isNotNull();
        assertThat(result.getId()).isEqualTo(incidentId);
        verify(incidentRepository, times(1)).findById(incidentId);
    }

    @Test
    void findById_shouldThrowNotFoundException_whenIdDoesNotExist() {
        // Arrange
        Long incidentId = 1L;
        when(incidentRepository.findById(incidentId)).thenReturn(Optional.empty());

        // Act & Assert
        assertThatThrownBy(() -> incidentService.findById(incidentId))
                .isInstanceOf(NotFoundException.class);

        // Assert
        verify(incidentRepository, times(1)).findById(incidentId);
    }

    @Test
    void getFilteredIncidents_shouldReturnPage_whenFilterIsValid() {
        // Arrange
        IncidentFilterDto filterDto = new IncidentFilterDto(
                IncidentStatus.OPEN, IncidentCategory.SOFTWARE,
                IncidentPriority.HIGH, ResponsibleService.DEVOPS_SERVICE,
                LocalDateTime.MIN, LocalDateTime.MAX);

        Pageable pageable = Pageable.unpaged();
        List<Incident> incidents = List.of(new Incident(), new Incident());
        Page<Incident> incidentPage = new PageImpl<>(incidents);
        Specification<Incident> mockSpecification = mock(Specification.class);

        when(specificationBuilder.build(filterDto)).thenReturn(mockSpecification);
        when(incidentRepository.findAll(eq(mockSpecification), eq(pageable))).thenReturn(incidentPage);

        IncidentDto dto1 = new IncidentDto(
                "Incident 1", "Description 1",
                LocalDateTime.now().minusDays(1), 1L,
                IncidentStatus.OPEN, IncidentPriority.HIGH,
                IncidentCategory.SOFTWARE, ResponsibleService.DEVOPS_SERVICE
        );

        IncidentDto dto2 = new IncidentDto(
                "Incident 2", "Description 2",
                LocalDateTime.now().minusDays(2), 2L,
                IncidentStatus.OPEN, IncidentPriority.HIGH,
                IncidentCategory.SOFTWARE, ResponsibleService.DEVOPS_SERVICE
        );

        when(incidentMapper.toDto(any(Incident.class))).thenReturn(dto1, dto2);

        // Act
        Page<IncidentDto> result = incidentService.getFilteredIncidents(filterDto, pageable);

        // Assert
        assertThat(result).isNotNull();
        assertThat(result.getTotalElements()).isEqualTo(2);
        assertThat(result.getContent()).containsExactly(dto1, dto2);

        verify(specificationBuilder, times(1)).build(filterDto);
        verify(incidentRepository, times(1)).findAll(eq(mockSpecification), eq(pageable));
        verify(incidentMapper, times(2)).toDto(any(Incident.class));
    }

    @Test
    void saveIncident_shouldReturnIncident_whenSaveSuccessfully() {
        // Arrange
        Long initiatorId = 1L;
        IncidentDto incidentDto = new IncidentDto(
                "Incident 1", "Description 1",
                LocalDateTime.now().minusDays(1), 2L,
                IncidentStatus.OPEN, IncidentPriority.HIGH,
                IncidentCategory.SOFTWARE, ResponsibleService.DEVOPS_SERVICE
        );

        Incident incident = new Incident(null,
                "Incident 1", "Description 1",
                LocalDateTime.now(), null,
                2L, initiatorId,
                IncidentStatus.OPEN, IncidentPriority.HIGH,
                IncidentCategory.SOFTWARE, ResponsibleService.DEVOPS_SERVICE
                );

        Incident savedIncident = incident;
        savedIncident.setId(100L);

        when(incidentMapper.toEntity(incidentDto)).thenReturn(incident);
        when(incidentRepository.save(incident)).thenReturn(savedIncident);

        // Act
        Incident result = incidentService.save(initiatorId, incidentDto);

        // Assert
        assertThat(result).isNotNull();
        assertThat(result.getId()).isEqualTo(100L);
        verify(incidentMapper, times(1)).toEntity(incidentDto);
        verify(incidentRepository, times(1)).save(incident);
    }

    @Test
    void save_shouldThrowExceptionWhenRepositoryFails() {
        // Arrange
        Long initiatorId = 100L;
        IncidentDto incidentDto = new IncidentDto(null, null, null, null,
                null, null,null,null);
        Incident incident = new Incident();

        when(incidentMapper.toEntity(incidentDto)).thenReturn(incident);
        when(incidentRepository.save(incident)).thenThrow(new DataAccessException("Database error") {});

        // Act & Assert
        assertThrows(DataAccessException.class, () ->
                incidentService.save(initiatorId, incidentDto)
        );
    }

    @Test
    void update_shouldUpdateIncidentSuccessfully() {
        // Arrange
        Long id = 100L;
        IncidentDto incidentDto = new IncidentDto("Updated Incident", "Updated Description", null, null,
                IncidentStatus.CLOSED, IncidentPriority.LOW,
                IncidentCategory.HARDWARE_PROBLEM, ResponsibleService.DEVELOPMENT_SERVICE);

        Incident existingIncident = new Incident();
        when(incidentRepository.findById(id)).thenReturn(Optional.of(existingIncident));

        Incident updatedIncident = new Incident();
        when(incidentRepository.save(existingIncident)).thenReturn(updatedIncident);
        when(incidentMapper.toDto(updatedIncident)).thenReturn(incidentDto);

        // Act
        IncidentDto result = incidentService.update(id, incidentDto);

        // Assert
        assertThat(result).isEqualTo(incidentDto);
        verify(incidentMapper, times(1)).updateIncidentFromDto(incidentDto, existingIncident);
        verify(incidentRepository, times(1)).save(existingIncident);
        verify(incidentMapper, times(1)).toDto(updatedIncident);
    }

    @Test
    void update_shouldThrowExceptionWhenIncidentNotFound() {
        // Arrange
        Long id = 100L;
        IncidentDto incidentDto = new IncidentDto(null, null, null, null,
                null, null,null,null);

        when(incidentRepository.findById(id)).thenThrow(new NotFoundException("Incident", id));

        // Act & Assert
        assertThrows(NotFoundException.class, () ->
                incidentService.update(id, incidentDto)
        );
    }
    @Test
    void update_shouldThrowExceptionWhenRepositoryFails() {
        // Arrange
        Long id = 100L;
        IncidentDto incidentDto = new IncidentDto(null, null, null, null,
                null, null,null,null);
        Incident existingIncident = new Incident();

        when(incidentRepository.findById(id)).thenReturn(Optional.of(existingIncident));
        doThrow(new DataAccessException("Database error") {}).when(incidentRepository).save(existingIncident);

        // Act & Assert
        assertThrows(DataAccessException.class, () ->
                incidentService.update(id, incidentDto)
        );
    }

    @Test
    void getFilteredIncidents_shouldThrowExceptionWhenRepositoryFails() {
        // Arrange
        IncidentFilterDto filterDto = new IncidentFilterDto(null, null, null,
                null, null, null);
        Pageable pageable = PageRequest.of(0, 10);

        Specification<Incident> specification = mock(Specification.class);
        when(specificationBuilder.build(filterDto)).thenReturn(specification);
        when(incidentRepository.findAll(specification, pageable)).thenThrow(new DataAccessException("Database error") {});

        // Act & Assert
        assertThrows(DataAccessException.class, () ->
                incidentService.getFilteredIncidents(filterDto, pageable)
        );
    }

    @Test
    void updateStatus_shouldUpdateIncidentStatus() {
        // Arrange
        Long id = 100L;
        IncidentStatus newStatus = IncidentStatus.CLOSED;
        Incident existingIncident = new Incident();
        when(incidentRepository.findById(id)).thenReturn(Optional.of(existingIncident));

        Incident updatedIncident = new Incident();
        updatedIncident.setStatus(newStatus);
        when(incidentRepository.save(existingIncident)).thenReturn(updatedIncident);
        when(incidentMapper.toDto(updatedIncident)).thenReturn(new IncidentDto(null, null, null, null,
                newStatus, null,null,null));

        // Act
        IncidentDto result = incidentService.updateStatus(id, newStatus);

        // Assert
        assertThat(updatedIncident.getStatus()).isEqualTo(newStatus);
        assertThat(result.status()).isEqualTo(newStatus);
        verify(incidentRepository, times(1)).save(existingIncident);
    }

    @Test
    void updateAnalyst_shouldUpdateIncidentAnalystId() {
        // Arrange
        Long id = 100L;
        Long analystId = 10L;
        Incident existingIncident = new Incident();
        when(incidentRepository.findById(id)).thenReturn(Optional.of(existingIncident));

        Incident updatedIncident = new Incident();
        updatedIncident.setAnalystId(analystId);
        when(incidentRepository.save(existingIncident)).thenReturn(updatedIncident);
        when(incidentMapper.toDto(updatedIncident)).thenReturn(new IncidentDto(null, null, null, analystId,
                null, null,null,null));

        // Act
        IncidentDto result = incidentService.updateAnalyst(id, analystId);

        // Assert
        assertThat(existingIncident.getAnalystId()).isEqualTo(analystId);
        assertThat(result.analystId()).isEqualTo(analystId);
        verify(incidentRepository, times(1)).save(existingIncident);
    }

    @Test
    void updatePriority_shouldUpdateIncidentPriority() {
        // Arrange
        Long id = 100L;
        IncidentPriority newPriority = IncidentPriority.HIGH;
        Incident existingIncident = new Incident();
        when(incidentRepository.findById(id)).thenReturn(Optional.of(existingIncident));

        Incident updatedIncident = new Incident();
        updatedIncident.setPriority(newPriority);
        when(incidentRepository.save(existingIncident)).thenReturn(updatedIncident);
        when(incidentMapper.toDto(updatedIncident)).thenReturn(new IncidentDto(null, null, null, null,
                null, newPriority, null, null));

        // Act
        IncidentDto result = incidentService.updatePriority(id, newPriority);

        // Assert
        assertThat(existingIncident.getPriority()).isEqualTo(newPriority);
        assertThat(result.priority()).isEqualTo(newPriority);
        verify(incidentRepository, times(1)).save(existingIncident);
    }

    @Test
    void updateCategory_shouldUpdateIncidentCategory() {
        // Arrange
        Long id = 100L;
        IncidentCategory newCategory = IncidentCategory.HARDWARE_PROBLEM;
        Incident existingIncident = new Incident();
        when(incidentRepository.findById(id)).thenReturn(Optional.of(existingIncident));

        Incident updatedIncident = new Incident();
        updatedIncident.setCategory(newCategory);
        when(incidentRepository.save(existingIncident)).thenReturn(updatedIncident);
        when(incidentMapper.toDto(updatedIncident)).thenReturn(new IncidentDto(null, null, null, null,
                null, null, newCategory, null));

        // Act
        IncidentDto result = incidentService.updateCategory(id, newCategory);

        // Assert
        assertThat(existingIncident.getCategory()).isEqualTo(newCategory);
        assertThat(result.category()).isEqualTo(newCategory);
        verify(incidentRepository, times(1)).save(existingIncident);
    }

    @Test
    void updateResponsibleService_shouldUpdateIncidentResponsibleService() {
        // Arrange
        Long id = 1L;
        ResponsibleService newService = ResponsibleService.ADMIN_SERVICE;

        Incident existingIncident = new Incident();
        existingIncident.setId(id);

        Incident updatedIncident = new Incident();
        updatedIncident.setId(id);
        updatedIncident.setResponsibleService(newService);

        when(incidentRepository.findById(id)).thenReturn(Optional.of(existingIncident));
        when(incidentRepository.save(existingIncident)).thenReturn(updatedIncident);
        when(incidentMapper.toDto(updatedIncident)).thenReturn(new IncidentDto(
                "test", "test", null, null,
                IncidentStatus.OPEN, IncidentPriority.HIGH,
                IncidentCategory.SOFTWARE, newService
        ));

        // Act
        IncidentDto result = incidentService.updateResponsibleService(id, newService);

        // Assert
        assertThat(existingIncident.getResponsibleService()).isEqualTo(newService);
        assertThat(result.responsibleService()).isEqualTo(newService);
        verify(incidentRepository, times(1)).save(existingIncident);
    }

    @Test
    void delete_shouldDeleteIncidentSuccessfully() {
        // Arrange
        Long id = 100L;
        doNothing().when(incidentRepository).deleteById(id);

        // Act
        incidentService.delete(id);

        // Assert
        verify(incidentRepository, times(1)).deleteById(id);
    }

    @Test
    void delete_shouldThrowNotFoundException_whenIncidentDoesNotExist() {
        // Arrange
        Long id = 100L;
        doThrow(new EmptyResultDataAccessException(1)).when(incidentRepository).deleteById(id);

        // Act & Assert
        assertThrows(NotFoundException.class, () -> incidentService.delete(id));
        verify(incidentRepository, times(1)).deleteById(id);
    }
}
*/