package co.com.crediya.r2dbc;

import java.util.UUID;

import org.springframework.data.repository.query.ReactiveQueryByExampleExecutor;
import org.springframework.data.repository.reactive.ReactiveCrudRepository;
import org.springframework.stereotype.Repository;

import co.com.crediya.r2dbc.entity.LoanAplicationEntity;

@Repository
public interface LoanAplicationReactiveRepository extends ReactiveCrudRepository<LoanAplicationEntity, UUID>, ReactiveQueryByExampleExecutor<LoanAplicationEntity> {

}
