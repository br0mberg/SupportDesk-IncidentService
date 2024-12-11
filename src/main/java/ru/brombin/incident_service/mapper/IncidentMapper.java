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

    default IncidentStatus getStatus(IncidentDto dto) {
        return dto.incidentStatus() != null ? dto.incidentStatus() : IncidentStatus.OPEN;
    }

    @Mapping(target="dateCreate", ignore = true)
    @Mapping(target="status", expression = "java(dto.incidentStatus() != null ? dto.incidentStatus() : entity.getStatus())")
    void updateIncidentFromDto(IncidentDto dto, @MappingTarget Incident entity);

    @Mapping(target = "incident", source = "incident")
    @Mapping(target = "imageDtos", source = "imageDtos")
    @Mapping(target = "analyst", source = "analystDto")
    @Mapping(target = "initiator", source = "initiatorDto")
    IncidentWithDetailsDto toIncidentWithDetailsDto(Incident incident,
                                                    List<ImageDto> imageDtos,
                                                    UserDto analystDto,
                                                    UserDto initiatorDto);
}
