package co.com.crediya.r2dbc;

import co.com.crediya.model.loanaplication.gateways.LoanAplicationRepository;
import co.com.crediya.model.loanaplication.loanAplication.LoanAplication;
import co.com.crediya.r2dbc.entity.LoanAplicationEntity;
import co.com.crediya.r2dbc.helper.ReactiveAdapterOperations;
import reactor.core.publisher.Mono;

import java.util.UUID;

import org.reactivecommons.utils.ObjectMapper;
import org.springframework.stereotype.Repository;

@Repository
public class LoanAplicationRepositoryAdapter extends ReactiveAdapterOperations<
    LoanAplication,
    LoanAplicationEntity,
    UUID,
    LoanAplicationReactiveRepository
> implements LoanAplicationRepository {
    
    public LoanAplicationRepositoryAdapter(LoanAplicationReactiveRepository repository, ObjectMapper mapper) {
        super(repository, mapper, d -> mapper.mapBuilder(d, LoanAplication.Builder.class).build());
    }

    @Override
    public Mono<LoanAplication> applyLoan(LoanAplication loanAplication) {
        return super.save(loanAplication);
    }
}
