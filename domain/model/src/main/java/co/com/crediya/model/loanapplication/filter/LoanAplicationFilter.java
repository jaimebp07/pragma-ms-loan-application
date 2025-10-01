package co.com.crediya.model.loanapplication.filter;

import java.util.Optional;
import java.util.UUID;

import co.com.crediya.model.loanapplication.LoanApplicationStatus;
import co.com.crediya.model.loanapplication.LoanType;

public record LoanAplicationFilter(
    Optional<LoanApplicationStatus> status,
    Optional<LoanType> loanType,
    Optional<UUID> customerId
) { }
