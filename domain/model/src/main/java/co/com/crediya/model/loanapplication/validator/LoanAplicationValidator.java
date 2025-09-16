package co.com.crediya.model.loanapplication.validator;

import java.math.BigDecimal;

import co.com.crediya.model.exceptions.BusinessException;
import co.com.crediya.model.exceptions.ErrorCode;
import co.com.crediya.model.loanapplication.LoanApplication;
import co.com.crediya.model.loanapplication.LoanType;

public class LoanAplicationValidator {

    public static void validate(LoanApplication loanAplication) {
        if (loanAplication.getLoanType() == null) {
            throw new BusinessException(ErrorCode.INVALID_ARGUMENT, "Loan type is required");
        }
        if (loanAplication.getClientId() == null || loanAplication.getClientId().toString().isEmpty()) {
            throw new BusinessException(ErrorCode.VALUE_REQUIRED, "Customer ID is required");
        }
        if (loanAplication.getAmount() == null || loanAplication.getAmount().compareTo(BigDecimal.ZERO) <= 0) {
            throw new BusinessException(ErrorCode.INVALID_ARGUMENT, "The amount must be greater than zero");
        }
        if (loanAplication.getTerm() == null || loanAplication.getTerm() <= 0) {
            throw new BusinessException(ErrorCode.INVALID_ARGUMENT, "The term must be greater than zero");
        }
        if (loanAplication.getLoanType() == null) {
            throw new BusinessException(ErrorCode.VALUE_REQUIRED,"The type of loan is mandatory");
        }

        LoanType.fromValue(loanAplication.getLoanType().name());
    }
}
