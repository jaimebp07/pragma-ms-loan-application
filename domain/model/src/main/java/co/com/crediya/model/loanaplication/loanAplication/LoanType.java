package co.com.crediya.model.loanaplication.loanAplication;

import java.util.UUID;

import co.com.crediya.model.loanaplication.ecxeptions.BusinessException;
import co.com.crediya.model.loanaplication.ecxeptions.ErrorCode;

public enum LoanType {
    
    PERSONAL("Personal Loan", UUID.fromString("a8f5d1a4-6b1b-4f39-8fa4-2c4dbd38d13e")),
    AUTO("Auto Loan", UUID.fromString("b3f2c13d-29c5-4c5e-93f1-1c8a7a63d0e9")),
    STUDENT("Student Loan", UUID.fromString("c47b9182-bc7d-4b86-b3e1-08f3b7e5d2a4")),
    BUSINESS("Business Loan", UUID.fromString("d1f27b71-9c46-4aa1-9f0e-3f4b1c693b90")),
    MICROCREDIT("Microcredit Loan", UUID.fromString("e73c9d29-2e6e-4dc0-bf07-41b77d43a1c2")),
    HOUSING("HOUSING Loan", UUID.fromString("f5a3d884-0fd8-4c1f-9de1-92bb8fa57ab3"));

    private final String description;
    private final UUID loanTypeId;

    LoanType(String description, UUID loanTypeId) {
        this.description = description;
        this.loanTypeId = loanTypeId;
    }

    public String getDescription() {
        return description;
    }
    public UUID getLoanTypeId(){
        return loanTypeId;
    }

    public static LoanType fromValue(String value) {
        for (LoanType type : values()) {
            if (type.name().equalsIgnoreCase(value) || 
                type.getDescription().equalsIgnoreCase(value)) {
                return type;
            }
        }
        throw new BusinessException(ErrorCode.INVALID_ARGUMENT, "Invalid loan type: " + value);
    }

    public static LoanType fromId(UUID id) {
        for (LoanType type : values()) {
            if (type.getLoanTypeId().equals(id)) {
                return type;
            }
        }
        throw new BusinessException(ErrorCode.INVALID_ARGUMENT,
                "Invalid loan type id: " + id);
    }
}
