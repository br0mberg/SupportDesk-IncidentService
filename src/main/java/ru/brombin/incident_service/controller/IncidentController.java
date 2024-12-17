package ru.brombin.incident_service.controller;

import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;
import ru.brombin.incident_service.dto.IncidentDto;
import ru.brombin.incident_service.dto.IncidentWithDetailsDto;
import ru.brombin.incident_service.entity.*;
import ru.brombin.incident_service.facade.IncidentFacade;
import ru.brombin.incident_service.service.IncidentService;

import java.util.List;

@Slf4j
@Validated
@RestController
@RequestMapping("/api/incidents")
@RequiredArgsConstructor
@FieldDefaults(level= AccessLevel.PRIVATE, makeFinal=true)
public class IncidentController {
    IncidentFacade incidentFacade;
    IncidentService incidentService;

    @PostMapping
    @PreAuthorize("hasAnyRole('ADMIN', 'ANALYST', 'USER')")
    public ResponseEntity<Incident> createIncident(@ModelAttribute IncidentDto incidentDto,
                                                   @RequestParam(value = "image_files", required = false) List<MultipartFile> images) {
        Incident incident = incidentFacade.createIncident(incidentDto, images);
        return ResponseEntity.status(HttpStatus.CREATED).body(incident);
    }

    @PutMapping("/{incidentId}")
    @PreAuthorize("hasAnyRole('ADMIN', 'ANALYST', 'USER')")
    public ResponseEntity<Incident> updateIncident(@PathVariable Long incidentId, @RequestBody IncidentDto incidentDto) {
        Incident updatedIncident = incidentFacade.updateIncident(incidentId, incidentDto);
        return ResponseEntity.ok(updatedIncident);
    }

    @DeleteMapping("/{incidentId}")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<Void> deleteIncident(@PathVariable Long incidentId) {
        incidentFacade.delete(incidentId);
        return ResponseEntity.noContent().build();
    }

    @GetMapping("/{incidentId}")
    @PreAuthorize("hasAnyRole('ADMIN', 'ANALYST')")
    public ResponseEntity<IncidentWithDetailsDto> getIncidentDetails(@PathVariable Long incidentId) {
        IncidentWithDetailsDto details = incidentFacade.findIncidentWithDetails(incidentId);
        return ResponseEntity.ok(details);
    }

    @GetMapping
    @PreAuthorize("hasAnyRole('ADMIN', 'ANALYST')")
    public ResponseEntity<Page<Incident>> getAllIncidents(
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size) {

        Page<Incident> incidents = incidentService.findAllWithPagination(page, size);
        return ResponseEntity.ok(incidents);
    }

    @PatchMapping("/{incidentId}/status")
    @PreAuthorize("hasAnyRole('ADMIN', 'ANALYST')")
    public ResponseEntity<Incident> assignStatus(@PathVariable Long incidentId, @RequestBody IncidentStatus status) {
        Incident updatedIncident = incidentService.updateStatus(incidentId, status);
        return ResponseEntity.ok(updatedIncident);
    }

    @PatchMapping("/{incidentId}/analyst")
    @PreAuthorize("hasAnyRole('ADMIN', 'ANALYST')")
    public ResponseEntity<Incident> assignAnalyst(@PathVariable Long incidentId, @RequestBody Long analystId) {
        Incident updatedIncident = incidentFacade.updateAnalyst(incidentId, analystId);
        return ResponseEntity.ok(updatedIncident);
    }

    @PatchMapping("/{incidentId}/priority")
    @PreAuthorize("hasAnyRole('ADMIN', 'ANALYST')")
    public ResponseEntity<Incident> assignPriority(@PathVariable Long incidentId, @RequestBody IncidentPriority priority) {
        Incident updatedIncident = incidentService.updatePriority(incidentId, priority);
        return ResponseEntity.ok(updatedIncident);
    }

    @PatchMapping("/{incidentId}/category")
    @PreAuthorize("hasAnyRole('ADMIN', 'ANALYST')")
    public ResponseEntity<Incident> assignCategory(@PathVariable Long incidentId, @RequestBody IncidentCategory category) {
        Incident updatedIncident = incidentService.updateCategory(incidentId, category);
        return ResponseEntity.ok(updatedIncident);
    }

    @PatchMapping("/{incidentId}/responsible-service")
    @PreAuthorize("hasAnyRole('ADMIN', 'ANALYST')")
    public ResponseEntity<Incident> assignResponsibleService(@PathVariable Long incidentId, @RequestBody ResponsibleService service) {
        Incident updatedIncident = incidentService.updateResponsibleService(incidentId, service);
        return ResponseEntity.ok(updatedIncident);
    }
}
