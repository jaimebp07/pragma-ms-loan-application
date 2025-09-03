package co.com.crediya.api.dto;

import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;

import java.math.BigDecimal;

import co.com.crediya.model.loanaplication.loanAplication.LoanType;

public record LoanAplicationDTO(
    @NotBlank(message = "The client id is required")
    String clientId,
    @NotNull(message = "The amount is required")
    @DecimalMin(value = "0.0", inclusive = false, message = "The amount must be positive")
    BigDecimal amount,
    @NotNull(message = "The term is required")
    @Min(value = 1, message = "The term must be at least 1")
    Integer term,
    @NotBlank(message = "The loan type is required")
    LoanType loanType
) { }
