package ru.brombin.incident_service.facade;

import org.springframework.web.multipart.MultipartFile;
import ru.brombin.incident_service.dto.IncidentDto;
import ru.brombin.incident_service.dto.IncidentWithDetailsDto;
import ru.brombin.incident_service.entity.Incident;

import java.util.List;

public interface IncidentFacade {
    Incident createIncident(IncidentDto incidentDto, List<MultipartFile> images);
    IncidentWithDetailsDto findIncidentWithDetails(Long incidentId);
    Incident updateAnalyst(Long incidentId, Long analystId);
    Incident updateIncident(Long incidentId, IncidentDto incidentDto);
    void delete(Long incidentId);
}
