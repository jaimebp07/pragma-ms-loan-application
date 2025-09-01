package co.com.crediya.api.dto;

import java.math.BigDecimal;

import co.com.crediya.model.loanaplication.loanAplication.LoanType;

public record LoanAplicationDTO(
    String clientId,
    BigDecimal amount,
    Integer term,
    LoanType loanType
) { }
