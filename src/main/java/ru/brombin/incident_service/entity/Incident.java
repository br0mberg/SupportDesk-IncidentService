package ru.brombin.incident_service.entity;

import jakarta.persistence.*;
import lombok.*;
import lombok.experimental.FieldDefaults;
import java.time.LocalDateTime;

import static lombok.AccessLevel.PRIVATE;

@Entity
@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Table(name="incidents")
@FieldDefaults(level = PRIVATE)
public class Incident {
    @Id
    @GeneratedValue(strategy= GenerationType.IDENTITY)
    Long id;

    String name;

    String description;

    LocalDateTime dateCreate;

    LocalDateTime dateClosed;

    Long analystId;

    Long initiatorId;

    @Enumerated(EnumType.STRING)
    IncidentStatus status;

    @Enumerated(EnumType.STRING)
    IncidentPriority priority;

    @Enumerated(EnumType.STRING)
    IncidentCategory category;

    @Enumerated(EnumType.STRING)
    ResponsibleService responsibleService;
}
