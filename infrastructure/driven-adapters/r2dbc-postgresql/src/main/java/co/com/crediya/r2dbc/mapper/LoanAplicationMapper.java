package co.com.crediya.r2dbc.mapper;

import java.util.Arrays;

import co.com.crediya.model.loanaplication.loanAplication.LoanAplication;
import co.com.crediya.model.loanaplication.loanAplication.LoanType;
import co.com.crediya.r2dbc.entity.LoanAplicationEntity;

public class LoanAplicationMapper {

    public static LoanAplicationEntity toEntity(LoanAplication domain) {
        LoanAplicationEntity entity = new LoanAplicationEntity();
        entity.setId(domain.getId());
        entity.setClientId(domain.getClientId());
        entity.setAmount(domain.getAmount());
        entity.setTerm(domain.getTerm());
        entity.setLoanType(domain.getLoanType().getLoanTypeId());
        entity.setStatus(domain.getStatus());
        return entity;
    }

    public static LoanAplication toDomain(LoanAplicationEntity entity) {
        LoanType type = Arrays.stream(LoanType.values())
                .filter(t -> t.getLoanTypeId().equals(entity.getLoanType()))
                .findFirst()
                .orElseThrow(() -> new IllegalArgumentException(
                        "No LoanType for id: " + entity.getLoanType()));

        return new LoanAplication.Builder()
                .id(entity.getId())
                .clientId(entity.getClientId())
                .amount(entity.getAmount())
                .term(entity.getTerm())
                .loanType(type)
                .status(entity.getStatus())
                .build();
    }
}
