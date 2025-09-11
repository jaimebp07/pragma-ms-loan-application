package co.com.crediya.api.security;

import java.util.UUID;

import co.com.crediya.model.loanaplication.gateways.TokenServiceGateway;
import org.springframework.security.core.context.ReactiveSecurityContextHolder;
import org.springframework.stereotype.Component;
import reactor.core.publisher.Mono;

@Component
public class TokenServiceAdapter implements TokenServiceGateway {
    
    @Override
    public Mono<UUID> getClientId() {
        return ReactiveSecurityContextHolder.getContext()
            .map(ctx -> ctx.getAuthentication().getPrincipal())
            .cast(Jwt.class)
            .map(jwt -> UUID.fromString(jwt.getClaimAsString("sub"))); 
    }
}
