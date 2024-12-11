package ru.brombin.incident_service.service;

import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import lombok.extern.slf4j.Slf4j;
import org.springframework.dao.EmptyResultDataAccessException;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.stereotype.Service;
import ru.brombin.incident_service.dto.IncidentDto;
import ru.brombin.incident_service.entity.*;
import ru.brombin.incident_service.mapper.IncidentMapper;
import ru.brombin.incident_service.repository.IncidentRepository;
import ru.brombin.incident_service.util.exceptions.NotFoundException;

import java.util.List;

import static lombok.AccessLevel.PRIVATE;

@Service
@Slf4j
@RequiredArgsConstructor
@FieldDefaults(level = PRIVATE, makeFinal = true)
public class IncidentServiceImpl implements IncidentService{

    IncidentRepository incidentRepository;
    IncidentMapper incidentMapper;

    @Override
    public Page<Incident> findAllWithPagination(int page, int size) {
        log.info("Fetching incidents with pagination: page={}, size={}", page, size);
        return incidentRepository.findAll(PageRequest.of(page, size));
    }

    @Override
    public List<Incident> findAll() {
        log.info("Fetching all incidents");
        return incidentRepository.findAll();
    }

    @Override
    public Incident findById(Long id) {
        log.info("Fetching incident with ID '{}'", id);
        return incidentRepository.findById(id).orElseThrow(() ->
                new NotFoundException("Incident not found with ID: " + id));
    }

    @Override
    public Incident save(Long initiatorId, IncidentDto incidentDto) {
        Incident incident = incidentMapper.toEntity(incidentDto);
        incident.setInitiatorId(initiatorId);

        Incident savedIncident = incidentRepository.save(incident);
        log.info("Incident '{}' has been successfully created with ID '{}'",
                savedIncident.getName(), savedIncident.getId());
        return savedIncident;
    }

    @Override
    public Incident update(Long id, IncidentDto incidentDto) {
        Incident existingIncident = findById(id);

        incidentMapper.updateIncidentFromDto(incidentDto, existingIncident);

        Incident updatedIncident = incidentRepository.save(existingIncident);
        log.info("Incident '{}' has been successfully updated with ID '{}'", updatedIncident.getName(), updatedIncident.getId());
        return updatedIncident;
    }

    @Override
    public Incident updateStatus(Long id, IncidentStatus status) {
        Incident existingIncident = findById(id);
        existingIncident.setStatus(status);

        Incident updatedIncident = incidentRepository.save(existingIncident);

        log.info("Incident status updated for ID '{}', new status='{}'", id, status);
        return updatedIncident;
    }

    @Override
    public Incident updateAnalyst(Long id, Long analystId) {
        Incident existingIncident = findById(id);
        existingIncident.setAnalystId(analystId);

        Incident updatedIncident = incidentRepository.save(existingIncident);

        log.info("Incident analyst updated for ID '{}', new analyst ID='{}'", id, analystId);
        return updatedIncident;
    }

    @Override
    public Incident updatePriority(Long id, IncidentPriority priority) {
        Incident existingIncident = findById(id);
        existingIncident.setPriority(priority);

        Incident updatedIncident = incidentRepository.save(existingIncident);

        log.info("Incident priority updated for ID '{}', new priority='{}'", id, priority);
        return updatedIncident;
    }

    @Override
    public Incident updateResponsibleService(Long id, ResponsibleService service) {
        Incident existingIncident = findById(id);
        existingIncident.setResponsibleService(service);

        Incident updatedIncident = incidentRepository.save(existingIncident);

        log.info("Incident responsible service updated for ID '{}', new service='{}'", id, service);
        return updatedIncident;
    }

    @Override
    public Incident updateCategory(Long incidentId, IncidentCategory category) {
        Incident existingIncident = findById(incidentId);
        existingIncident.setCategory(category);

        Incident updatedIncident = incidentRepository.save(existingIncident);

        log.info("Incident category updated for ID '{}', new category='{}'", incidentId, category);
        return updatedIncident;
    }

    @Override
    public void delete(Long id) {
        try {
            incidentRepository.deleteById(id);
            log.info("Incident with ID '{}' has been deleted", id);
        } catch (EmptyResultDataAccessException e) {
            throw new NotFoundException("Incident with ID " + id + " not found");
        }
    }
}
