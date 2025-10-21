package co.com.crediya.r2dbc;

import java.util.UUID;

import org.springframework.data.r2dbc.repository.Query;
import org.springframework.data.repository.query.ReactiveQueryByExampleExecutor;
import org.springframework.data.repository.reactive.ReactiveCrudRepository;
import org.springframework.stereotype.Repository;

import co.com.crediya.r2dbc.entity.LoanAplicationEntity;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

@Repository
public interface LoanAplicationReactiveRepository extends ReactiveCrudRepository<LoanAplicationEntity, UUID>, ReactiveQueryByExampleExecutor<LoanAplicationEntity> {
    @Query("SELECT automatic_validation FROM credi_ya.loan_type WHERE id = :loanTypeId")
    Mono<Boolean> findAutomaticValidationByLoanTypeId(UUID loanTypeId);
    
    @Query("""
           SELECT * 
           FROM credi_ya.loan_applications
           WHERE client_id = :customerId
             AND status = 'APPROVED'
           """)
    Flux<LoanAplicationEntity> findApprovedLoansByClientId(UUID customerId);
}
