package co.com.crediya.model.loanaplication.loanAplication;

import co.com.crediya.model.loanaplication.ecxeptions.BusinessException;

public enum LoanAplicationStatus {

    APPROVED ("Approved"),
    REJECTED ("Rejected"),
    PENDING ("Pending");

    private final String description;

    LoanAplicationStatus(String description) {
        this.description = description;
    }

    public String getDescription() {
        return description;
    }

    public static LoanAplicationStatus fromValue(String value) {
        for (LoanAplicationStatus type : values()) {
            if (type.name().equalsIgnoreCase(value) || 
                type.getDescription().equalsIgnoreCase(value)) {
                return type;
            }
        }
        throw new BusinessException("Invalid status type: " + value);
    }
}
