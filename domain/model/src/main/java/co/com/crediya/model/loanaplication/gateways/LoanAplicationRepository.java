package co.com.crediya.model.loanaplication.gateways;

import co.com.crediya.model.loanaplication.filter.LoanAplicationFilter;
import co.com.crediya.model.loanaplication.loanAplication.LoanAplication;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

public interface LoanAplicationRepository {
    Mono<LoanAplication> applyLoan(LoanAplication loanAplication);
    Flux<LoanAplication> findPaged(int page, int size, LoanAplicationFilter filter);
    Mono<Long> count(LoanAplicationFilter filter);
}
