package ru.brombin.incident_service.service;

import org.springframework.data.domain.Page;
import ru.brombin.incident_service.dto.IncidentDto;
import ru.brombin.incident_service.entity.*;

import java.util.List;

public interface IncidentService {
    Incident findById(Long id);
    List<Incident> findAll();
    Page<Incident> findAllWithPagination(int page, int size);
    Incident save(Long initiatorId, IncidentDto incidentDto);
    Incident update(Long id, IncidentDto incidentDto);
    Incident updateStatus(Long id, IncidentStatus status);
    Incident updateAnalyst(Long id, Long analystId);
    Incident updatePriority(Long id, IncidentPriority priority);
    Incident updateResponsibleService(Long id, ResponsibleService service);
    Incident updateCategory(Long incidentId, IncidentCategory category);
    void delete(Long id);
}
