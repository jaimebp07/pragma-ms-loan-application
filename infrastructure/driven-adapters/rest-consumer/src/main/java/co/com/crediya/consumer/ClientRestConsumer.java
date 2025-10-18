package co.com.crediya.consumer;

import io.github.resilience4j.circuitbreaker.annotation.CircuitBreaker;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

import java.util.Set;
import java.util.UUID;

import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.http.HttpStatusCode;
import org.springframework.stereotype.Service;
import org.springframework.web.reactive.function.client.ClientResponse;
import org.springframework.web.reactive.function.client.WebClient;

import co.com.crediya.consumer.DTO.GetListUsersRqDTO;
import co.com.crediya.model.customer.Customer;
import co.com.crediya.model.customer.gateways.CustomerGateway;
import co.com.crediya.model.exceptions.BusinessException;
import co.com.crediya.model.exceptions.ErrorCode;
import co.com.crediya.model.security.TokenServiceGateway;
import reactor.core.publisher.Mono;

@Slf4j
@Service
@RequiredArgsConstructor
public class ClientRestConsumer implements CustomerGateway {

    private final @Qualifier("authWebClient") WebClient client;
    private final TokenServiceGateway tokenService;

    @Override
    @CircuitBreaker(name = "clientService", fallbackMethod = "fallbackExistsById")
    public Mono<Boolean> existsById(UUID clientId) {
        log.info("Consulting client existence with id={}", clientId);
        return client.get()
                .uri("/api/v1/usuarios/{id}/exists", clientId)
                .exchangeToMono(resp -> {
                    return handleUserExistsResponse(resp);
                });
    }

    @SuppressWarnings("unused")
    private Mono<Boolean> fallbackExistsById(UUID clientId, Throwable ex) {
        log.error("Fallback for existsById: clientId={} - cause={}", clientId, ex.toString());
        return Mono.error(new RuntimeException("User service unavailable", ex));
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
 
    @Override
    @CircuitBreaker(name = "clientService", fallbackMethod = "fallbackFindByIdList")
    public Mono<Set<Customer>> findByIdList(Set<UUID> customerIds) {
        log.info("Fetching user list for {} ids", customerIds.size());
        return tokenService.getRawToken()
                    .flatMap(token -> 
                        client.post()
                            .uri("/api/v1/usuarios/page")
                            .header("Authorization", "Bearer " + token)
                            .bodyValue(new GetListUsersRqDTO(customerIds))
                            .retrieve()
                            .onStatus(HttpStatusCode::is4xxClientError, resp ->
                                resp.bodyToMono(String.class)
                                    .defaultIfEmpty("Client error")
                                    .flatMap(msg ->
                                        Mono.error(new BusinessException(
                                            ErrorCode.VALIDATION_ERROR,
                                            "Error 4xx al consultar usuarios: " + msg))
                                    )
                            )
                            .onStatus(HttpStatusCode::is5xxServerError, resp ->
                                resp.bodyToMono(String.class)
                                    .defaultIfEmpty("Server error")
                                    .flatMap(msg ->
                                        Mono.error(new RuntimeException(
                                            "Error 5xx del microservicio de autenticación: " + msg))
                                    )
                            )
                            .bodyToMono(new ParameterizedTypeReference<Set<Customer>>() {})
                    );
    }

    @SuppressWarnings("unused")
    private Mono<Set<Customer>> fallbackFindByIdList(Set<UUID> ids, Throwable ex) {
        log.error("Fallback for findByIdList: {}", ex.getMessage());
        return Mono.error(new RuntimeException("User service unavailable", ex));
    }

    private record ExistsResponse(boolean exists, String message) {}

}
