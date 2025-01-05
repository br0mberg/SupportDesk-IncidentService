package ru.brombin.incident_service.service;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import ru.brombin.incident_service.dto.IncidentDto;
import ru.brombin.incident_service.dto.IncidentFilterDto;
import ru.brombin.incident_service.entity.*;

public interface IncidentService {
    Page<IncidentDto> getFilteredIncidents(IncidentFilterDto incidentFilterDto, Pageable pageable);
    Incident findById(Long id);
    Incident save(Long initiatorId, IncidentDto incidentDto);
    IncidentDto update(Long id, IncidentDto incidentDto);
    IncidentDto updateStatus(Long id, IncidentStatus status);
    IncidentDto updateAnalyst(Long id, Long analystId);
    IncidentDto updatePriority(Long id, IncidentPriority priority);
    IncidentDto updateResponsibleService(Long id, ResponsibleService service);
    IncidentDto updateCategory(Long incidentId, IncidentCategory category);
    void delete(Long id);
}
