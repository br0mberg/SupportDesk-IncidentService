package ru.brombin.incident_service.facade;

import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.collections4.CollectionUtils;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;
import ru.brombin.incident_service.dto.*;
import ru.brombin.incident_service.entity.Incident;
import ru.brombin.incident_service.mapper.ImageMapper;
import ru.brombin.incident_service.mapper.IncidentMapper;
import ru.brombin.incident_service.repository.IncidentRepository;
import ru.brombin.incident_service.security.JwtTokenProvider;
import ru.brombin.incident_service.service.grpc.GrpcClientService;
import ru.brombin.incident_service.service.kafka.KafkaImageService;
import ru.brombin.incident_service.service.IncidentService;
import ru.brombin.incident_service.service.UserService;
import ru.brombin.incident_service.util.exceptions.NotFoundException;

import java.util.Collections;
import java.util.List;
import java.util.Objects;
import java.util.Optional;

import static lombok.AccessLevel.PRIVATE;

@Service
@Slf4j
@RequiredArgsConstructor
@FieldDefaults(level=PRIVATE, makeFinal=true)
public class IncidentFacadeImpl implements IncidentFacade {
    UserService userService;
    IncidentService incidentService;
    KafkaImageService kafkaImageService;
    IncidentRepository incidentRepository;
    ImageMapper imageMapper;
    IncidentMapper incidentMapper;
    JwtTokenProvider jwtTokenProvider;
    GrpcClientService grpcClientService;

    @Override
    @Transactional
    public IncidentDto createIncident(IncidentDto incidentDto, List<MultipartFile> images) {
        Long initiatorId = userService.getCurrentUserId();
        Incident incident = incidentService.save(initiatorId, incidentDto);

        List<ImageDto> imageDtoList = processImages(incident.getId(), images);

        if (CollectionUtils.isNotEmpty(imageDtoList)) {
            grpcClientService.uploadImages(imageDtoList, incident);
        }

        log.info("Incident '{}' created with ID '{}' and linked to '{}' images",
                incident.getName(), incident.getId(), imageDtoList.size());
        return incidentMapper.toDto(incident);
    }

    private List<ImageDto> processImages(Long incidentId, List<MultipartFile> images) {
        if (CollectionUtils.isEmpty(images)) {
            return Collections.emptyList();
        }

        return images.stream()
                .map(image -> imageMapper.toImageDto(image, incidentId))
                .filter(Objects::nonNull)
                .toList();
    }

    @Override
    @Transactional
    public IncidentWithDetailsDto findIncidentWithDetails(Long incidentId) {
        Incident incident = incidentService.findById(incidentId);
        UserDto initiatorDto = userService.findById(incident.getInitiatorId())
                .orElseThrow(() -> new NotFoundException("User", incident.getInitiatorId()));

        Optional<UserDto> analystDto = userService.findById(incident.getAnalystId());

        List<ImageDto> imagesDto = imageMapper.toImageDtoList(grpcClientService.findImagesByIncidentId(incidentId));

        return incidentMapper.toIncidentWithDetailsDto(
                incident,
                imagesDto,
                analystDto.orElse(null),
                initiatorDto
        );
    }

    @Override
    @Transactional
    public IncidentDto updateAnalyst(Long incidentId, Long analystId) {
        userService.findById(analystId).orElseThrow(() -> new NotFoundException("User", analystId));
        return incidentService.updateAnalyst(incidentId, analystId);
    }

    @Override
    @Transactional
    public IncidentDto updateIncident(Long incidentId, IncidentDto incidentDto) {
        String currentUserRole = userService.getCurrentUserRole();
        Incident existingIncident = incidentService.findById(incidentId);

        if (currentUserRole.equals("ROLE_USER")) {
            existingIncident.setName(incidentDto.name());
            existingIncident.setDescription(incidentDto.description());
            return incidentMapper.toDto(incidentRepository.save(existingIncident));
        }

        return incidentService.update(incidentId, incidentDto);
    }

    @Override
    @Transactional
    public void delete(Long incidentId) {
        incidentService.findById(incidentId);

        kafkaImageService.deleteImage(new DeleteImageRequest(incidentId), jwtTokenProvider.extractJwtTokenValue());

        incidentService.delete(incidentId);
        log.info("Incident with ID '{}' and associated images deleted", incidentId);
    }
}
