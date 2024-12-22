package ru.brombin.incident_service.config;

import image.ImageServiceGrpc;
import image.ImageServiceGrpc.*;
import user.UserServiceGrpc;
import user.UserServiceGrpc.*;
import io.grpc.ManagedChannel;
import io.grpc.ManagedChannelBuilder;
import lombok.AccessLevel;
import lombok.experimental.FieldDefaults;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
@FieldDefaults(level= AccessLevel.PRIVATE)
public class GrpcConfig {

    @Value("${grpc.client.image-service.host}")
    String imageHost;

    @Value("${grpc.client.image-service.port}")
    Integer imagePort;

    @Value("${grpc.client.user-service.host}")
    String userHost;

    @Value("${grpc.client.user-service.port}")
    Integer userPort;

    @Bean
    public ManagedChannel imageServiceChannel() {
        return ManagedChannelBuilder.forAddress(imageHost, imagePort)
                .usePlaintext()
                .build();
    }

    @Bean
    public ImageServiceBlockingStub imageServiceStub(@Qualifier("imageServiceChannel") ManagedChannel channel) {
        return ImageServiceGrpc.newBlockingStub(channel);
    }

    @Bean
    public ManagedChannel userServiceChannel() {
        return ManagedChannelBuilder.forAddress(userHost, userPort)
                .usePlaintext()
                .build();
    }

    @Bean
    public UserServiceBlockingStub userServiceBlockingStub(@Qualifier("userServiceChannel")ManagedChannel channel) {
        return UserServiceGrpc.newBlockingStub(channel);
    }
}