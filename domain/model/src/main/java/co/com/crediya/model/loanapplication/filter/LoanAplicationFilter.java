package co.com.crediya.model.loanapplication.filter;

import java.util.Optional;

import co.com.crediya.model.loanapplication.LoanType;

public record LoanAplicationFilter(
    Optional<String> status,
    Optional<LoanType> loanType
) { }
