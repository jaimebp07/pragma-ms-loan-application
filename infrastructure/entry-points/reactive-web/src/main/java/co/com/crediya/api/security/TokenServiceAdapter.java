package co.com.crediya.api.security;

import java.util.UUID;

import co.com.crediya.model.loanaplication.gateways.TokenServiceGateway;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.authentication.AuthenticationCredentialsNotFoundException;
import org.springframework.security.core.context.ReactiveSecurityContextHolder;
import org.springframework.security.core.context.SecurityContext;
import org.springframework.stereotype.Component;
import reactor.core.publisher.Mono;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.security.oauth2.server.resource.authentication.JwtAuthenticationToken;


@Component
public class TokenServiceAdapter implements TokenServiceGateway {
    
    private static final Logger log = LoggerFactory.getLogger(TokenServiceAdapter.class);

    @Value("${jwt.secret}")
    private String secret;

    @Override
    public Mono<UUID> getAuthUserId() {
        return ReactiveSecurityContextHolder.getContext()
            .map(SecurityContext::getAuthentication)
            .filter(auth -> auth instanceof JwtAuthenticationToken)
            .map(auth -> (JwtAuthenticationToken) auth)
            .map(jwt -> UUID.fromString(jwt.getToken().getSubject()))
            .doOnError(e -> log.error("Error al obtener el id del usuario en el token", e))
            .onErrorResume(e -> {
                return Mono.error(new AuthenticationCredentialsNotFoundException("Error getting user ID from token"));
            });
    }

}
