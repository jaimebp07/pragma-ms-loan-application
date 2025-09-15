package co.com.crediya.api.dto;

import co.com.crediya.model.loanaplication.filter.LoanAplicationFilter;
import jakarta.validation.constraints.NotNull;

public record PagedLoanApplicationRqDTO(
    @NotNull(message = "The page is required")
    Integer page,
    @NotNull(message = "The size is required")
    Integer size,
    LoanAplicationFilter filter
) { }
