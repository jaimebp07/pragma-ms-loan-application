package co.com.crediya.model.customer.gateways;

import java.util.Set;
import java.util.UUID;

import co.com.crediya.model.customer.Customer;
import reactor.core.publisher.Mono;

public interface CustomerGateway {
    Mono<Boolean> existsById(UUID clientId);
    Mono<Set<Customer>> findByIdList(Set<UUID> customerIds);
}

