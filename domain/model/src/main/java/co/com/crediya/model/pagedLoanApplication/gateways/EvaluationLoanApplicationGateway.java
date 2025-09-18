package co.com.crediya.model.pagedLoanApplication.gateways;

import co.com.crediya.model.loanapplication.filter.LoanAplicationFilter;
import co.com.crediya.model.pagedLoanApplication.EvaluationLoanApplication;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

public interface EvaluationLoanApplicationGateway {
    Flux<EvaluationLoanApplication> findPaged(int page, int size, LoanAplicationFilter filter);
    Mono<Long> count(LoanAplicationFilter filter);
}
