package co.com.crediya.usecase.applyloan;

import co.com.crediya.model.loanaplication.ecxeptions.BusinessException;
import co.com.crediya.model.loanaplication.ecxeptions.ErrorCode;
import co.com.crediya.model.loanaplication.gateways.ClientRepository;
import co.com.crediya.model.loanaplication.gateways.LoanAplicationRepository;
import co.com.crediya.model.loanaplication.gateways.TokenServiceGateway;
import co.com.crediya.model.loanaplication.loanAplication.LoanAplication;
import co.com.crediya.model.loanaplication.loanAplication.LoanAplicationStatus;
import co.com.crediya.model.loanaplication.loanAplication.validator.LoanAplicationValidator;
import reactor.core.publisher.Mono;

public class ApplyLoanUseCase {

    private final LoanAplicationRepository loanAplicationRepository;
    private final ClientRepository clientRepository;
    private final TokenServiceGateway tokenServiceGateway;

    public ApplyLoanUseCase(LoanAplicationRepository loanAplicationRepository, ClientRepository clientRepository, TokenServiceGateway tokenServiceGateway) {
        this.loanAplicationRepository = loanAplicationRepository;
        this.clientRepository = clientRepository;
        this.tokenServiceGateway = tokenServiceGateway;
        
    }

    public Mono<LoanAplication> applyLoan(LoanAplication loanAplication) {

        return tokenServiceGateway.getClientId().flatMap(clientId -> {
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
                    if (ex.getMessage() != null && ex.getMessage().contains("R2DBC")) {
                        return new BusinessException(ErrorCode.DB_ERROR, "Database connection failed");
                    }
                    return new BusinessException(ErrorCode.UNEXPECTED_ERROR, "Unexpected error: " + ex.getMessage());
                });
    }

    private LoanAplication withPendingStatus(LoanAplication loanAplication) {
        return loanAplication.toBuilder()
                .status(LoanAplicationStatus.PENDING)
                .build();
    }
}
