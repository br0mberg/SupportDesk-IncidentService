package ru.brombin.incident_service.facade;

import org.springframework.web.multipart.MultipartFile;
import ru.brombin.incident_service.dto.IncidentDto;
import ru.brombin.incident_service.dto.IncidentWithDetailsDto;
import ru.brombin.incident_service.entity.Incident;

import java.util.List;

public interface IncidentFacade {
    IncidentDto createIncident(IncidentDto incidentDto, List<MultipartFile> images);
    IncidentWithDetailsDto findIncidentWithDetails(Long incidentId);
    IncidentDto updateAnalyst(Long incidentId, Long analystId);
    IncidentDto updateIncident(Long incidentId, IncidentDto incidentDto);
    void delete(Long incidentId);
}
