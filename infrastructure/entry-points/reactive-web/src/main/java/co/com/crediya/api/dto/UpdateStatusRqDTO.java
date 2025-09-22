package co.com.crediya.api.dto;

import java.util.UUID;
import jakarta.validation.constraints.NotNull;

import co.com.crediya.model.loanapplication.LoanApplicationStatus;

public record UpdateStatusRqDTO(
        @NotNull UUID loanId,
        @NotNull LoanApplicationStatus status,
        String comment
) {}
