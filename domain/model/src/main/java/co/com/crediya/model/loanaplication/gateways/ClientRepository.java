package co.com.crediya.model.loanaplication.gateways;

import java.util.UUID;

import reactor.core.publisher.Mono;

public interface ClientRepository {
    Mono<Boolean> existsById(UUID clientId);
}
