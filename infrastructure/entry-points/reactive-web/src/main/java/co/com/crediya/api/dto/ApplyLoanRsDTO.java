package co.com.crediya.api.dto;

import java.math.BigDecimal;
import java.util.UUID;

import co.com.crediya.model.loanapplication.LoanApplicationStatus;
import io.swagger.v3.oas.annotations.media.Schema;

public record ApplyLoanRsDTO(

    @Schema(description = "Loan application id", example = "35a13a2d-b1f0-44f9-983a-97603855be62")
    UUID id,

    @Schema(description = "Client id", example = "35a13a2d-b1f0-44f9-983a-97603855be62")
    UUID clientId,

    @Schema(description = "Amount of the loan", example = "1000.00")
    BigDecimal amount,

    @Schema(description = "Loan term in months", example = "12")
    Integer term,

    @Schema(description = "Loan type", example = "PERSONAL")
    String loanType,

    @Schema(
        description = "Loan application status (always starts as PENDING)",
        example = "PENDING"
    )
    LoanApplicationStatus status
) { }