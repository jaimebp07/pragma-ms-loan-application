package co.com.crediya.model.loanaplication.filter;

import java.util.Optional;

import co.com.crediya.model.loanaplication.loanAplication.LoanType;

public record LoanAplicationFilter(
    Optional<String> status,
    Optional<LoanType> loanType
) { }
