package co.com.crediya.model.pagedLoanApplication.gateways;

import reactor.core.publisher.Mono;

public interface LoanDecisionPublisherGateway {
    Mono<String> publish(String eventJson);
}
