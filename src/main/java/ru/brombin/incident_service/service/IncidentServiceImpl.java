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
import ru.brombin.incident_service.util.messages.IncidentLogMessages;

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
        log.info(IncidentLogMessages.INCIDENT_FETCH_PAGINATED.getFormatted(page, size));
        return incidentRepository.findAll(PageRequest.of(page, size));
    }

    @Override
    public Incident findById(Long id) {
        return incidentRepository.findById(id).orElseThrow(() ->
                new NotFoundException(IncidentLogMessages.INCIDENT_NOT_FOUND.getFormatted(id)));
    }

    @Override
    public Incident save(Long initiatorId, IncidentDto incidentDto) {
        Incident incident = incidentMapper.toEntity(incidentDto);
        incident.setInitiatorId(initiatorId);

        Incident savedIncident = incidentRepository.save(incident);
        log.info(IncidentLogMessages.INCIDENT_CREATED.getFormatted(savedIncident.getId()));
        return savedIncident;
    }

    @Override
    public Incident update(Long id, IncidentDto incidentDto) {
        Incident existingIncident = findById(id);

        incidentMapper.updateIncidentFromDto(incidentDto, existingIncident);

        Incident updatedIncident = incidentRepository.save(existingIncident);
        log.info(IncidentLogMessages.INCIDENT_UPDATED.getFormatted(updatedIncident.getId()));
        return updatedIncident;
    }

    @Override
    public Incident updateStatus(Long id, IncidentStatus status) {
        Incident existingIncident = findById(id);
        existingIncident.setStatus(status);

        Incident updatedIncident = incidentRepository.save(existingIncident);

        log.info(IncidentLogMessages.INCIDENT_STATUS_UPDATED.getFormatted(id, status));
        return updatedIncident;
    }

    @Override
    public Incident updateAnalyst(Long id, Long analystId) {
        Incident existingIncident = findById(id);
        existingIncident.setAnalystId(analystId);

        Incident updatedIncident = incidentRepository.save(existingIncident);

        log.info(IncidentLogMessages.INCIDENT_ANALYST_UPDATED.getFormatted(id, analystId));
        return updatedIncident;
    }

    @Override
    public Incident updatePriority(Long id, IncidentPriority priority) {
        Incident existingIncident = findById(id);
        existingIncident.setPriority(priority);

        Incident updatedIncident = incidentRepository.save(existingIncident);

        log.info(IncidentLogMessages.INCIDENT_PRIORITY_UPDATED.getFormatted(id, priority));
        return updatedIncident;
    }

    @Override
    public Incident updateResponsibleService(Long id, ResponsibleService service) {
        Incident existingIncident = findById(id);
        existingIncident.setResponsibleService(service);

        Incident updatedIncident = incidentRepository.save(existingIncident);

        log.info(IncidentLogMessages.INCIDENT_RESPONSIBLE_SERVICE_UPDATED.getFormatted(id, service));
        return updatedIncident;
    }

    @Override
    public Incident updateCategory(Long incidentId, IncidentCategory category) {
        Incident existingIncident = findById(incidentId);
        existingIncident.setCategory(category);

        Incident updatedIncident = incidentRepository.save(existingIncident);

        log.info(IncidentLogMessages.INCIDENT_CATEGORY_UPDATED.getFormatted(incidentId, category));
        return updatedIncident;
    }

    @Override
    public void delete(Long id) {
        try {
            incidentRepository.deleteById(id);
            log.info(IncidentLogMessages.INCIDENT_DELETED.getFormatted(id));
        } catch (EmptyResultDataAccessException e) {
            throw new NotFoundException(IncidentLogMessages.INCIDENT_NOT_FOUND.getFormatted(id));
        }
    }
}
