package co.com.crediya.model.loanapplication.gateways;

import co.com.crediya.model.loanapplication.LoanApplication;
import co.com.crediya.model.loanapplication.filter.LoanAplicationFilter;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

public interface LoanAplicationRepository {
    Mono<LoanApplication> applyLoan(LoanApplication loanAplication);
    Flux<LoanApplication> findPaged(int page, int size, LoanAplicationFilter filter);
    Mono<Long> count(LoanAplicationFilter filter);
}
