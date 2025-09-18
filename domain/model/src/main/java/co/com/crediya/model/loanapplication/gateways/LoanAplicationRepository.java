package co.com.crediya.model.loanapplication.gateways;

import co.com.crediya.model.loanapplication.LoanApplication;
import reactor.core.publisher.Mono;

public interface LoanAplicationRepository {
    Mono<LoanApplication> applyLoan(LoanApplication loanAplication);
}
