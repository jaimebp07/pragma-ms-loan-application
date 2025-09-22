package co.com.crediya.model.loanapplication.gateways;

import java.util.Optional;
import java.util.UUID;

import co.com.crediya.model.loanapplication.LoanApplication;
import co.com.crediya.model.loanapplication.LoanApplicationStatus;
import reactor.core.publisher.Mono;

public interface LoanAplicationRepository {
    Mono<LoanApplication> applyLoan(LoanApplication loanAplication);
    Mono<LoanApplication> updateLoanAplicationStatus(UUID loanApplicationID, LoanApplicationStatus status, Optional<String> comment);
}
