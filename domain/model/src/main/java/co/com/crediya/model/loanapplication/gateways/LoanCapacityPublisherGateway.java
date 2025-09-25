package co.com.crediya.model.loanapplication.gateways;

import co.com.crediya.model.loanapplication.LoanApplicationStatus;
import reactor.core.publisher.Mono;

public interface LoanCapacityPublisherGateway {
    Mono<LoanApplicationStatus> validateLoanPublish(String eventJson);
}
