package co.com.crediya.model.security;

import reactor.core.publisher.Mono;
import java.util.UUID;

public interface TokenServiceGateway {
    Mono<UUID> getAuthUserId();
}
