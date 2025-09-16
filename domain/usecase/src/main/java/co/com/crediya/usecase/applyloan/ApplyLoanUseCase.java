package co.com.crediya.usecase.applyloan;

import co.com.crediya.model.customer.gateways.CustomerGateway;
import co.com.crediya.model.exceptions.BusinessException;
import co.com.crediya.model.exceptions.ErrorCode;
import co.com.crediya.model.loanapplication.LoanApplication;
import co.com.crediya.model.loanapplication.LoanApplicationStatus;
import co.com.crediya.model.loanapplication.gateways.LoanAplicationRepository;
import co.com.crediya.model.loanapplication.validator.LoanAplicationValidator;
import co.com.crediya.model.security.TokenServiceGateway;
import reactor.core.publisher.Mono;

public class ApplyLoanUseCase {

    private final LoanAplicationRepository loanAplicationRepository;
    private final CustomerGateway clientRepository;
    private final TokenServiceGateway tokenServiceGateway;

    public ApplyLoanUseCase(LoanAplicationRepository loanAplicationRepository, CustomerGateway clientRepository, TokenServiceGateway tokenServiceGateway) {
        this.loanAplicationRepository = loanAplicationRepository;
        this.clientRepository = clientRepository;
        this.tokenServiceGateway = tokenServiceGateway;
        
    }

    public Mono<LoanApplication> applyLoan(LoanApplication loanAplication) {

        return tokenServiceGateway.getAuthUserId().flatMap(clientId -> {
                        if(!loanAplication.getClientId().equals(clientId)){
                            return Mono.error(new BusinessException(ErrorCode.UNAUTHORIZED, 
                                "The request client does not match the token"));
                        }
                        return Mono.just(loanAplication);
                    }
                )
                .doOnNext(LoanAplicationValidator::validate)
                .flatMap(app -> clientRepository.existsById(app.getClientId())
                        .flatMap(exists -> {
                            if (!exists) {
                                return Mono.error(new BusinessException(ErrorCode.CLIENT_NOT_FOUND, "Client not found"));
                            }
                            return Mono.just(app);
                        })
                )
                .map(this::withPendingStatus)
                .flatMap(loanAplicationRepository::applyLoan)
                .onErrorMap(ex -> {
                    if (ex instanceof BusinessException) {
                        return ex;
                    }
                    if (ex.getMessage() != null && ex.getMessage().contains("R2DBC")) {
                        return new BusinessException(ErrorCode.DB_ERROR, "Database connection failed");
                    }
                    return new BusinessException(ErrorCode.UNEXPECTED_ERROR, "Unexpected error: " + ex.getMessage());
                });
    }

    private LoanApplication withPendingStatus(LoanApplication loanAplication) {
        return loanAplication.toBuilder()
                .status(LoanApplicationStatus.PENDING)
                .build();
    }
}
