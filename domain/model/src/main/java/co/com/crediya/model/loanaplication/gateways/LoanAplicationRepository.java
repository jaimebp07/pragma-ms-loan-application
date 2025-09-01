package co.com.crediya.model.loanaplication.gateways;

import co.com.crediya.model.loanaplication.loanAplication.LoanAplication;
import reactor.core.publisher.Mono;

public interface LoanAplicationRepository {
    Mono<LoanAplication> applyLoan(LoanAplication loanAplication);
}
