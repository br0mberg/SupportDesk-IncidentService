package ru.brombin.incident_service.service;

import io.grpc.*;
import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Component;
import ru.brombin.incident_service.security.JwtTokenProvider;

@Component
@FieldDefaults(level = AccessLevel.PRIVATE)
public class JwtClientInterceptor implements ClientInterceptor {

    @Override
    public <Q, P> ClientCall<Q, P> interceptCall(MethodDescriptor<Q, P> methodDescriptor,
                                                 CallOptions callOptions,
                                                 Channel channel) {
        return new ForwardingClientCall.SimpleForwardingClientCall<>(channel.newCall(methodDescriptor, callOptions)) {
            @Override
            public void start(Listener<P> responseListener, Metadata headers) {
                Metadata.Key<String> authKey = Metadata.Key.of("Authorization", Metadata.ASCII_STRING_MARSHALLER);
                headers.put(authKey, "Bearer " + new JwtTokenProvider().extractJwtTokenValue());
                super.start(responseListener, headers);
            }
        };
    }
}
