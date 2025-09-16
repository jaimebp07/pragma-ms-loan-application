package co.com.crediya.model.loanapplication;

import co.com.crediya.model.exceptions.BusinessException;

public enum LoanApplicationStatus {

    APPROVED ("Approved"),
    REJECTED ("Rejected"),
    PENDING ("Pending");

    private final String description;

    LoanApplicationStatus(String description) {
        this.description = description;
    }

    public String getDescription() {
        return description;
    }

    public static LoanApplicationStatus fromValue(String value) {
        for (LoanApplicationStatus type : values()) {
            if (type.name().equalsIgnoreCase(value) || 
                type.getDescription().equalsIgnoreCase(value)) {
                return type;
            }
        }
        throw new BusinessException("Invalid status type: " + value);
    }
}
