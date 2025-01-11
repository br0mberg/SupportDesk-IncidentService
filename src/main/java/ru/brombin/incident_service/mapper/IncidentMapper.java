package ru.brombin.incident_service.mapper;

import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.MappingTarget;
import ru.brombin.incident_service.dto.ImageDto;
import ru.brombin.incident_service.dto.IncidentDto;
import ru.brombin.incident_service.dto.IncidentWithDetailsDto;
import ru.brombin.incident_service.dto.UserDto;
import ru.brombin.incident_service.entity.Incident;
import ru.brombin.incident_service.entity.IncidentStatus;

import java.util.List;


@Mapper(componentModel = "spring")
public interface IncidentMapper {

    @Mapping(target="dateCreate", expression = "java(java.time.LocalDateTime.now())")
    @Mapping(target="status", expression = "java(getStatus(dto))")
    Incident toEntity(IncidentDto dto);

    IncidentDto toDto(Incident incident);

    default IncidentStatus getStatus(IncidentDto dto) {
        return dto.status() != null ? dto.status() : IncidentStatus.OPEN;
    }

    @Mapping(target="dateCreate", ignore = true)
    @Mapping(target="status", expression = "java(getStatus(dto))")
    void updateIncidentFromDto(IncidentDto dto, @MappingTarget Incident entity);

    IncidentWithDetailsDto toIncidentWithDetailsDto(IncidentDto incidentDto,
                                                    List<ImageDto> imageDtos,
                                                    UserDto analystDto,
                                                    UserDto initiatorDto);
}
