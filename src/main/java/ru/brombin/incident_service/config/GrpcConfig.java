package ru.brombin.incident_service.config;

import image.ImageServiceGrpc;
import io.grpc.ManagedChannel;
import io.grpc.ManagedChannelBuilder;
import lombok.AccessLevel;
import lombok.experimental.FieldDefaults;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import ru.brombin.incident_service.service.JwtClientInterceptor;

@Configuration
@FieldDefaults(level= AccessLevel.PRIVATE)
public class GrpcConfig {

    @Value("${grpc.client.image-service.host}")
    String imageHost;

    @Value("${grpc.client.image-service.port}")
    Integer imagePort;

    @Bean
    public ManagedChannel imageServiceChannel() {
        return ManagedChannelBuilder.forAddress(imageHost, imagePort)
                .usePlaintext()
                .build();
    }

    @Bean
    public ImageServiceGrpc.ImageServiceBlockingStub imageServiceStub(ManagedChannel channel) {
        return ImageServiceGrpc.newBlockingStub(channel);
    }
}