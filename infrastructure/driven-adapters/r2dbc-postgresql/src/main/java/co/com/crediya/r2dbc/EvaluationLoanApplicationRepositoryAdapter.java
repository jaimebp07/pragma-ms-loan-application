package co.com.crediya.r2dbc;

import java.util.ArrayList;
import java.util.List;
import java.math.BigDecimal;
import java.util.UUID;

import org.springframework.data.r2dbc.core.R2dbcEntityTemplate;
import org.springframework.data.relational.core.query.Criteria;
import org.springframework.data.relational.core.query.Query;
import org.springframework.stereotype.Repository;

import co.com.crediya.model.loanapplication.LoanApplication;
import co.com.crediya.model.loanapplication.LoanApplicationStatus;
import co.com.crediya.model.loanapplication.LoanType;
import co.com.crediya.model.loanapplication.filter.LoanAplicationFilter;
import co.com.crediya.model.pagedLoanApplication.EvaluationLoanApplication;
import co.com.crediya.model.pagedLoanApplication.gateways.EvaluationLoanApplicationGateway;
import co.com.crediya.r2dbc.entity.LoanAplicationEntity;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

@Repository
public class EvaluationLoanApplicationRepositoryAdapter implements EvaluationLoanApplicationGateway {

    private final R2dbcEntityTemplate template;

    public EvaluationLoanApplicationRepositoryAdapter( R2dbcEntityTemplate template ) {
        this.template = template;
    }

    @Override
    public Flux<EvaluationLoanApplication> findPaged(int page, int size, LoanAplicationFilter filter) {
        
        StringBuilder sql = buildSqlConsult(filter);
        
        var spec = template.getDatabaseClient()
                .sql(sql.toString())
                .bind("limit", size)
                .bind("offset", page * size);

        if (filter.status().isPresent()) {
            spec = spec.bind("status", filter.status().get().name());
        }
        if (filter.loanType().isPresent()) {
            spec = spec.bind("loanType", filter.loanType().get().name());
        }

        if(filter.customerId().isPresent()) {
            spec = spec.bind("clientId", filter.customerId().get());
        }

        return spec.map((row, metadata) -> {
            LoanApplication loan = new LoanApplication.Builder()
                    .id(row.get("loan_id", UUID.class))
                    .clientId(row.get("client_id", UUID.class))
                    .amount(row.get("amount", BigDecimal.class))
                    .term(row.get("term", Integer.class))
                    .loanType(LoanType.valueOf(row.get("loan_type_name", String.class)))
                    .status(LoanApplicationStatus.valueOf(row.get("status", String.class)))
                    .build();

            BigDecimal interestRate = row.get("interest_rate", BigDecimal.class);

            return new EvaluationLoanApplication.Builder()
                    .loanApplication(loan)
                    .interestRate(interestRate)
                    .build();
        }).all();
    }

    private StringBuilder buildSqlConsult(LoanAplicationFilter filter) {
        StringBuilder sql = new StringBuilder("""
            SELECT la.id AS loan_id,
                la.client_id,
                la.amount,
                la.term,
                la.status,
                la.loan_type_id,
                lt.name AS loan_type_name,
                lt.interest_rate AS interest_rate
            FROM credi_ya.loan_applications la
            JOIN credi_ya.loan_type lt ON la.loan_type_id = lt.id
        """);

        
        List<String> conditions = new ArrayList<>();

        if (filter.status().isPresent()) {
            conditions.add("la.status = :status");
        }
        if (filter.loanType().isPresent()) {
            conditions.add("lt.name = :loanType");
        }

        if (filter.customerId().isPresent()) {
            conditions.add("la.client_id = :clientId");
        }

        if (!conditions.isEmpty()) {
            sql.append(" WHERE ")
            .append(String.join(" AND ", conditions));
        }

        sql.append(" ORDER BY la.created_at DESC LIMIT :limit OFFSET :offset");

        return sql;
    }

    @Override
    public Mono<Long> count(LoanAplicationFilter filter) {

        List<Criteria> criteriaList = new ArrayList<>();
        filter.status().ifPresent(s -> criteriaList.add(Criteria.where("status").is(s)));
        filter.loanType().ifPresent(t -> criteriaList.add(Criteria.where("loan_type_id").is(t.getLoanTypeId())));

        Criteria criteria = criteriaList.isEmpty()
                ? Criteria.empty()
                : Criteria.from(criteriaList.toArray(new Criteria[0]));

        Query query = Query.query(criteria);
        return template.count(query, LoanAplicationEntity.class);
    }

}
