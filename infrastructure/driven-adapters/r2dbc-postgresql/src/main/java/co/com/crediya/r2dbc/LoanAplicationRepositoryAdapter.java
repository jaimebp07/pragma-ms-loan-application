package co.com.crediya.r2dbc;

import co.com.crediya.model.loanaplication.gateways.LoanAplicationRepository;
import co.com.crediya.model.loanaplication.loanAplication.LoanAplication;
import co.com.crediya.r2dbc.entity.LoanAplicationEntity;
import co.com.crediya.r2dbc.mapper.LoanAplicationMapper;
import lombok.extern.slf4j.Slf4j;
import reactor.core.publisher.Mono;


import org.springframework.stereotype.Repository;

@Slf4j
@Repository
public class LoanAplicationRepositoryAdapter implements LoanAplicationRepository {
    
    private final LoanAplicationReactiveRepository repository;

    public LoanAplicationRepositoryAdapter(LoanAplicationReactiveRepository repository) {
        this.repository = repository;
    }

    @Override
    public Mono<LoanAplication> applyLoan(LoanAplication loanAplication) {
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
}
