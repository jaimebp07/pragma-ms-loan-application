package co.com.crediya.api.dto;

import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;

import java.math.BigDecimal;
import java.util.UUID;

import co.com.crediya.model.loanaplication.loanAplication.LoanType;
import io.swagger.v3.oas.annotations.media.Schema;

public record ApplyLoanRqDTO(
    @Schema(description = "Client id", example = "35a13a2d-b1f0-44f9-983a-97603855be62")
    @NotBlank(message = "The client id is required")
    UUID clientId,
    @NotNull(message = "The amount is required")
    @DecimalMin(value = "0.0", inclusive = false, message = "The amount must be positive")
    BigDecimal amount,
    @NotNull(message = "The term is required")
    @Min(value = 1, message = "The term must be at least 1")
    Integer term,
    @NotBlank(message = "The loan type is required")
    LoanType loanType
) { }
