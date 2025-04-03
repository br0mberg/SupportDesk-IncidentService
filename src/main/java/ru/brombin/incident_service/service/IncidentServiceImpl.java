package ru.brombin.incident_service.service;

import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import lombok.extern.slf4j.Slf4j;
import org.springframework.dao.EmptyResultDataAccessException;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Service;
import ru.brombin.incident_service.builder.IncidentSpecificationBuilder;
import ru.brombin.incident_service.dto.IncidentDto;
import ru.brombin.incident_service.dto.IncidentFilterDto;
import ru.brombin.incident_service.entity.*;
import ru.brombin.incident_service.mapper.IncidentMapper;
import ru.brombin.incident_service.repository.IncidentRepository;
import ru.brombin.incident_service.util.exceptions.NotFoundException;

import static lombok.AccessLevel.PRIVATE;

@Service
@Slf4j
@RequiredArgsConstructor
@FieldDefaults(level = PRIVATE, makeFinal = true)
public class IncidentServiceImpl implements IncidentService{

    IncidentRepository incidentRepository;
    IncidentMapper incidentMapper;
    IncidentSpecificationBuilder specificationBuilder;

    @Override
    public Page<IncidentDto> getFilteredIncidents(IncidentFilterDto filterDto, Pageable pageable) {
        Specification<Incident> specification = specificationBuilder.build(filterDto);
        Page<Incident> incidents = incidentRepository.findAll(specification, pageable);

        log.info("Filtered incidents retrieved successfully with filters: {} and page request: {}",
                filterDto, pageable);
        return incidents.map(incidentMapper::toDto);
    }

    @Override
    public Incident findById(Long id) {
        Incident incident = incidentRepository.findById(id).orElseThrow(() ->
                new NotFoundException("Incident", id));
        log.info("Incident with id: {} found successfully", id);
        return incident;
    }

    @Override
    public Incident save(Long initiatorId, IncidentDto incidentDto) {
        Incident incident = incidentMapper.toEntity(incidentDto);
        incident.setInitiatorId(initiatorId);
        if (incident.getStatus().describeConstable().isEmpty()) incident.setStatus(IncidentStatus.OPEN);

        Incident savedIncident = incidentRepository.save(incident);
        log.info("Incident with ID: {} created successfully", savedIncident.getId());
        return savedIncident;
    }

    @Override
    public IncidentDto update(Long id, IncidentDto incidentDto) {
        Incident existingIncident = findById(id);
        incidentMapper.updateIncidentFromDto(incidentDto, existingIncident);

        Incident updatedIncident = incidentRepository.save(existingIncident);

        log.info("Incident with ID: {} updated successfully", id);
        return incidentMapper.toDto(updatedIncident);
    }

    @Override
    public IncidentDto updateStatus(Long id, IncidentStatus status) {
        Incident existingIncident = findById(id);
        existingIncident.setStatus(status);

        Incident updatedIncident = incidentRepository.save(existingIncident);

        log.info("Incident status updated for ID {}, new status={}", id, status);
        return incidentMapper.toDto(updatedIncident);
    }

    @Override
    public IncidentDto updateAnalyst(Long id, Long analystId) {
        Incident existingIncident = findById(id);
        existingIncident.setAnalystId(analystId);

        Incident updatedIncident = incidentRepository.save(existingIncident);

        log.info("Incident analyst updated for ID {}, new analyst ID={}", id, analystId);
        return incidentMapper.toDto(updatedIncident);
    }

    @Override
    public IncidentDto updatePriority(Long id, IncidentPriority priority) {
        Incident existingIncident = findById(id);
        existingIncident.setPriority(priority);

        Incident updatedIncident = incidentRepository.save(existingIncident);

        log.info("Incident priority updated for ID {}, new priority={}", id, priority);
        return incidentMapper.toDto(updatedIncident);
    }

    @Override
    public IncidentDto updateResponsibleService(Long id, ResponsibleService service) {
        Incident existingIncident = findById(id);
        existingIncident.setResponsibleService(service);

        Incident updatedIncident = incidentRepository.save(existingIncident);

        log.info("Incident responsible service updated for ID {}, new service={}", id, service);
        return incidentMapper.toDto(updatedIncident);
    }

    @Override
    public IncidentDto updateCategory(Long incidentId, IncidentCategory category) {
        Incident existingIncident = findById(incidentId);
        existingIncident.setCategory(category);

        Incident updatedIncident = incidentRepository.save(existingIncident);

        log.info( "Incident category updated for ID {}, new category={}", updatedIncident.getId(), category);
        return incidentMapper.toDto(updatedIncident);
    }

    @Override
    public void delete(Long id) {
        try {
            incidentRepository.deleteById(id);
            log.info("Incident with id: {} deleted successfully", id);
        } catch (EmptyResultDataAccessException e) {
            throw new NotFoundException("Incident", id);
        }
    }
}
