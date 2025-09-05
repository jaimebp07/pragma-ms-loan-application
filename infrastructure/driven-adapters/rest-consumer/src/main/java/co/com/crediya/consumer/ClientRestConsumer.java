package co.com.crediya.consumer;

import io.github.resilience4j.circuitbreaker.annotation.CircuitBreaker;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

import java.util.UUID;

import org.springframework.stereotype.Service;
import org.springframework.web.reactive.function.client.ClientResponse;
import org.springframework.web.reactive.function.client.WebClient;
import reactor.core.publisher.Mono;

import co.com.crediya.model.loanaplication.gateways.ClientRepository;

@Slf4j
@Service
@RequiredArgsConstructor
public class ClientRestConsumer implements ClientRepository {

    private final WebClient client;

    @Override
    @CircuitBreaker(name = "clientService", fallbackMethod = "fallbackExistsById")
    public Mono<Boolean> existsById(UUID clientId) {
        log.info("Consulting client existence with id={}", clientId);
        return client.get()
                .uri("/api/v1/usuarios/{id}", clientId)
                .exchangeToMono(resp -> {
                    return handleUserExistsResponse(resp);
                });
    }

    private Mono<Boolean> handleUserExistsResponse(ClientResponse resp) {
        if (resp.statusCode().is2xxSuccessful()) {
            return resp.bodyToMono(ExistsResponse.class).map(ExistsResponse::exists);
        }
        if (resp.statusCode().value() == 404) {
            return Mono.just(false);
        }
        return resp.createException().flatMap(Mono::error);
    }

    private Mono<Boolean> fallbackExistsById(UUID clientId, Throwable ex) {
        log.warn("Fallback clientService → devolviendo false para clientId={} por error: {}",
                clientId, ex.toString());
        return Mono.just(false);
    }

    private record ExistsResponse(boolean exists, String message) {}

    
}
