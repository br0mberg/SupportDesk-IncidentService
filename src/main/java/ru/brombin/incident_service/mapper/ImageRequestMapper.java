package ru.brombin.incident_service.mapper;

import com.google.protobuf.ByteString;
import image.ImageServiceOuterClass.SaveImageRequest;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import ru.brombin.incident_service.dto.ImageDto;

@Mapper(componentModel = "spring", imports = ByteString.class)
public interface ImageRequestMapper {

    @Mapping(target = "fileData", expression = "java(ByteString.copyFrom(imageDto.content()))")
    SaveImageRequest toSaveImageRequest(ImageDto imageDto);
}