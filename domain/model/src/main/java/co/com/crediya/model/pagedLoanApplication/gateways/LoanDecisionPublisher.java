package co.com.crediya.model.pagedLoanApplication.gateways;

import reactor.core.publisher.Mono;

public interface LoanDecisionPublisher {
    Mono<String> publish(String eventJson);
}
