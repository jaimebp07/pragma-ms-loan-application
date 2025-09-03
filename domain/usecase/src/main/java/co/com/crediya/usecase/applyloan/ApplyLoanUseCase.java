package co.com.crediya.usecase.applyloan;

import co.com.crediya.model.loanaplication.ecxeptions.BusinessException;
import co.com.crediya.model.loanaplication.gateways.LoanAplicationRepository;
import co.com.crediya.model.loanaplication.loanAplication.LoanAplication;
import co.com.crediya.model.loanaplication.loanAplication.LoanAplicationStatus;
import co.com.crediya.model.loanaplication.loanAplication.validator.LoanAplicationValidator;
import reactor.core.publisher.Mono;

public class ApplyLoanUseCase {

    private final LoanAplicationRepository loanAplicationRepository;

    public ApplyLoanUseCase(LoanAplicationRepository loanAplicationRepository) {
        this.loanAplicationRepository = loanAplicationRepository;
    }

    public Mono<LoanAplication> applyLoan(LoanAplication loanAplication) {
        return Mono.just(loanAplication)
                .doOnNext(LoanAplicationValidator::validate)
                .map(this::withPendingStatus)
                .flatMap(loanAplicationRepository::applyLoan)
                .onErrorMap(ex -> {
                    System.out.println("Error occurred: " + ex.getMessage());
                    if (ex.getMessage() != null && ex.getMessage().contains("R2DBC")) {
                        return new BusinessException("Database connection failed", "DB_CONNECTION_ERROR");
                    }
                    return new BusinessException("Unexpected error: " + ex.getMessage(), "UNEXPECTED_ERROR");
                });
    }

    private LoanAplication withPendingStatus(LoanAplication loanAplication) {
        return loanAplication.toBuilder()
                .status(LoanAplicationStatus.PENDING)
                .build();
    }
}
