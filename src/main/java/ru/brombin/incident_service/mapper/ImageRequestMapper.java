package ru.brombin.incident_service.mapper;

import com.google.protobuf.ByteString;
import image.ImageServiceOuterClass.SaveImageRequest;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import ru.brombin.incident_service.dto.ImageDto;

@Mapper(componentModel = "spring", imports = ByteString.class)
public interface ImageRequestMapper {

    @Mapping(target = "incidentId", source = "incidentId")
    @Mapping(target = "size", source = "size")
    @Mapping(target = "type", source = "type")
    @Mapping(target = "fileData", expression = "java(ByteString.copyFrom(imageDto.content()))")
    SaveImageRequest toSaveImageRequest(ImageDto imageDto);
}