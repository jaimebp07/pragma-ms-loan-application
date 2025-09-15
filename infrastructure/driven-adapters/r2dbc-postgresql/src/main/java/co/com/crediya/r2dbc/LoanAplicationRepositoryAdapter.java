package co.com.crediya.r2dbc;

import co.com.crediya.model.loanaplication.filter.LoanAplicationFilter;
import co.com.crediya.model.loanaplication.gateways.LoanAplicationRepository;
import co.com.crediya.model.loanaplication.loanAplication.LoanAplication;
import co.com.crediya.r2dbc.entity.LoanAplicationEntity;
import co.com.crediya.r2dbc.mapper.LoanAplicationMapper;
import lombok.extern.slf4j.Slf4j;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import java.util.ArrayList;
import java.util.List;

import org.springframework.data.domain.PageRequest;
import org.springframework.data.r2dbc.core.R2dbcEntityTemplate;
import org.springframework.data.relational.core.query.Query;
import org.springframework.data.relational.core.query.Criteria;
import org.springframework.stereotype.Repository;

@Slf4j
@Repository
public class LoanAplicationRepositoryAdapter implements LoanAplicationRepository {
    
    private final LoanAplicationReactiveRepository repository;
    private final R2dbcEntityTemplate template;

    public LoanAplicationRepositoryAdapter(LoanAplicationReactiveRepository repository, R2dbcEntityTemplate template) {
        this.repository = repository;
        this.template = template;
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

    @Override
    public Flux<LoanAplication> findPaged(int page, int size, LoanAplicationFilter filter) {
        
        List<Criteria> criteriaList = new ArrayList<>();
        filter.status().ifPresent(s -> criteriaList.add(Criteria.where("status").is(s)));
        filter.loanType().ifPresent(t -> criteriaList.add(Criteria.where("loan_type").is(t.name())));

        Criteria criteria = criteriaList.isEmpty()
                ? Criteria.empty()
                : Criteria.from(criteriaList.toArray(new Criteria[0]));

        log.debug("Executing paged query: page={}, size={}, filter={}", page, size, filter);

        Query query = Query.query(criteria)
                        .with(PageRequest.of(page, size));

        return template.select(query, LoanAplicationEntity.class)
                    .map(LoanAplicationMapper::toDomain);
    }

    @Override
    public Mono<Long> count(LoanAplicationFilter filter) {

        List<Criteria> criteriaList = new ArrayList<>();
        filter.status().ifPresent(s -> criteriaList.add(Criteria.where("status").is(s)));
        filter.loanType().ifPresent(t -> criteriaList.add(Criteria.where("loan_type").is(t.name())));

        Criteria criteria = criteriaList.isEmpty()
                ? Criteria.empty()
                : Criteria.from(criteriaList.toArray(new Criteria[0]));

        Query query = Query.query(criteria);
        return template.count(query, LoanAplicationEntity.class);
    }
}
