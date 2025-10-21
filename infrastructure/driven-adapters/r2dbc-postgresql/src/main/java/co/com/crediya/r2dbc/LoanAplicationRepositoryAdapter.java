package co.com.crediya.r2dbc;

import co.com.crediya.model.exceptions.BusinessException;
import co.com.crediya.model.exceptions.ErrorCode;
import co.com.crediya.model.loanapplication.LoanApplication;
import co.com.crediya.model.loanapplication.LoanApplicationStatus;
import co.com.crediya.model.loanapplication.LoanType;
import co.com.crediya.model.loanapplication.gateways.LoanAplicationRepository;
import co.com.crediya.r2dbc.entity.LoanAplicationEntity;
import co.com.crediya.r2dbc.mapper.LoanAplicationMapper;
import lombok.extern.slf4j.Slf4j;
import reactor.core.publisher.Mono;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

import org.springframework.stereotype.Repository;

@Slf4j
@Repository
public class LoanAplicationRepositoryAdapter implements LoanAplicationRepository {
    
    private final LoanAplicationReactiveRepository repository;

    public LoanAplicationRepositoryAdapter(LoanAplicationReactiveRepository repository) {
        this.repository = repository;
    }

    @Override
    public Mono<LoanApplication> applyLoan(LoanApplication loanAplication) {
        log.info("Saving Loan Application: clientId={}, amount={}, term={}, loanType={}, status={}",
                loanAplication.getClientId(),
                loanAplication.getAmount(),
                loanAplication.getTerm(),
                loanAplication.getLoanType(),
                loanAplication.getStatus());

        LoanAplicationEntity entity = LoanAplicationMapper.toEntity(loanAplication);
        
        return repository.save(entity)
                .map(LoanAplicationMapper::toDomain)
                .doOnSuccess(saved -> 
                    log.info("Loan Application saved successfully: id={}, clientId={}, amount={}, status={}",
                            saved.getId(),
                            saved.getClientId(),
                            saved.getAmount(),
                            saved.getStatus()
                    )
                )
                .doOnError(ex -> 
                    log.error("Failed to save Loan Application for clientId={} due to: {}", 
                            loanAplication.getClientId(), ex.getMessage(), ex)
                );
    }

    @Override
    public Mono<LoanApplication> updateLoanAplicationStatus(UUID loanApplicationID, LoanApplicationStatus status,
            Optional<String> comment) {
        return repository.findById(loanApplicationID)
        .switchIfEmpty(Mono.error(new IllegalArgumentException("Solicitud no encontrada")))
        .flatMap(entity -> {
            entity.setStatus(status);
            return repository.save(entity);
        })
        .map(LoanAplicationMapper::toDomain)
        .doOnSuccess(updated ->
            log.info("Status updated: id={}, newStatus={}", updated.getId(), updated.getStatus())
        );
    }

    @Override
    public Mono<Boolean> isAutomaticValidation(LoanType loanType) {
    return repository.findAutomaticValidationByLoanTypeId(loanType.getLoanTypeId())
        .switchIfEmpty(Mono.error(new BusinessException(
            ErrorCode.LOAN_TYPE_NOT_FOUND,
            "Tipo de préstamo no encontrado: " + loanType.name()
        )));
    }

    @Override
    public Mono<List<LoanApplication>> getLoansAprovedByCustomer(UUID customerId) {
        return repository.findApprovedLoansByClientId(customerId)
            .map(LoanAplicationMapper::toDomain)
            .collectList(); 
    }
}
