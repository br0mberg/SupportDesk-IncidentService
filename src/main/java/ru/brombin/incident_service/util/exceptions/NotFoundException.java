package ru.brombin.incident_service.util.exceptions;

import lombok.AccessLevel;
import lombok.Getter;
import lombok.experimental.FieldDefaults;

@Getter
@FieldDefaults(level= AccessLevel.PRIVATE, makeFinal = true)
public class NotFoundException extends RuntimeException {
    String entityName;
    Long identifier;

    public NotFoundException(String entityName, Long identifier) {
        super(String.format("%s with identifier [%s] not found", entityName, identifier));
        this.entityName = entityName;
        this.identifier = identifier;
    }
}
