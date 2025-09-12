package co.com.crediya.model.loanaplication.gateways;

import reactor.core.publisher.Mono;
import java.util.UUID;

public interface TokenServiceGateway {
    Mono<UUID> getAuthUserId();
}
