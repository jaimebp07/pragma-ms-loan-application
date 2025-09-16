package co.com.crediya.model.customer.gateways;

import java.util.UUID;

import reactor.core.publisher.Mono;

public interface CustomerGateway {
    Mono<Boolean> existsById(UUID clientId);
}

