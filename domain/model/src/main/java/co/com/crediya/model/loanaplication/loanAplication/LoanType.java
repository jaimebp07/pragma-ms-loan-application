package co.com.crediya.model.loanaplication.loanAplication;

import co.com.crediya.model.loanaplication.ecxeptions.BusinessException;

public enum LoanType {
    
    PERSONAL("Personal Loan"),
    MORTGAGE("Mortgage Loan"),
    AUTO("Auto Loan"),
    STUDENT("Student Loan"),
    BUSINESS("Business Loan"),
    CONSUMER("Consumer Loan"),
    CREDIT_LINE("Credit Line"),
    DEBT_CONSOLIDATION("Debt Consolidation");

    private final String description;

    LoanType(String description) {
        this.description = description;
    }

    public String getDescription() {
        return description;
    }

    public static LoanType fromValue(String value) {
        for (LoanType type : values()) {
            if (type.name().equalsIgnoreCase(value) || 
                type.getDescription().equalsIgnoreCase(value)) {
                return type;
            }
        }
        throw new BusinessException("Invalid loan type: " + value);
    }
}
