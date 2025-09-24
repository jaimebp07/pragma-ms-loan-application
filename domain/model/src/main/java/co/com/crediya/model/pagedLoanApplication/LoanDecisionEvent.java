package co.com.crediya.model.pagedLoanApplication;

import java.time.Instant;
import java.util.UUID;

public record LoanDecisionEvent(
    UUID loanId,
    String email,
    String status,
    String name,
    Instant decisionDate
) {
    public String toJson() {
        return String.format(
            "{\"loanId\":\"%s\",\"email\":\"%s\",\"status\":\"%s\",\"name\":\"%s\",\"decisionDate\":\"%s\"}",
            loanId,
            email,
            status,
            name,
            decisionDate
        );
    }
 }
