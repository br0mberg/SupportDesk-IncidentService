package ru.brombin.incident_service.mapper;

import image.ImageServiceOuterClass.ImageData;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.Named;
import org.springframework.stereotype.Component;
import org.springframework.web.multipart.MultipartFile;
import ru.brombin.incident_service.dto.ImageDto;
import ru.brombin.incident_service.util.exceptions.ImageProcessingException;

import java.io.IOException;
import java.util.List;

@Mapper(componentModel = "spring")
public interface ImageMapper {

    @Mapping(target = "incidentId", source = "incidentId")
    @Mapping(target = "fileName", expression = "java(file.getOriginalFilename())")
    @Mapping(target = "size", expression = "java(file.getSize())")
    @Mapping(target = "type", expression = "java(file.getContentType())")
    @Mapping(target = "content", source = "file", qualifiedByName = "extractFileData")
    ImageDto toImageDto(MultipartFile file, Long incidentId);

    @Mapping(target = "incidentId", source = "incidentId")
    @Mapping(target = "fileName", source = "fileName")
    @Mapping(target = "size", source = "size")
    @Mapping(target = "type", source = "type")
    @Mapping(target = "content", expression = "java(image.getFileData().toByteArray())")
    ImageDto toImageDto(ImageData image);

    List<ImageDto> toImageDtoList(List<ImageData> images);

    @Named("extractFileData")
    default byte[] extractFileData(MultipartFile file) {
        try {
            return file.getBytes();
        } catch (IOException e) {
            throw new RuntimeException("Failed to extract file data from MultipartFile: " + file.getOriginalFilename(), e);
        }
    }
}
