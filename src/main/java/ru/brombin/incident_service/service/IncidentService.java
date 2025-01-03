package ru.brombin.incident_service.service;

import org.springframework.data.domain.Page;
import ru.brombin.incident_service.dto.IncidentDto;
import ru.brombin.incident_service.entity.*;

public interface IncidentService {
    Incident findById(Long id);
    Page<IncidentDto> findAllWithPagination(int page, int size);
    Incident save(Long initiatorId, IncidentDto incidentDto);
    IncidentDto update(Long id, IncidentDto incidentDto);
    IncidentDto updateStatus(Long id, IncidentStatus status);
    IncidentDto updateAnalyst(Long id, Long analystId);
    IncidentDto updatePriority(Long id, IncidentPriority priority);
    IncidentDto updateResponsibleService(Long id, ResponsibleService service);
    IncidentDto updateCategory(Long incidentId, IncidentCategory category);
    void delete(Long id);
}
