package co.com.crediya.model.loanaplication.loanAplication.validator;

import java.math.BigDecimal;

import co.com.crediya.model.loanaplication.ecxeptions.BusinessException;
import co.com.crediya.model.loanaplication.ecxeptions.ErrorCode;
import co.com.crediya.model.loanaplication.loanAplication.LoanAplication;
import co.com.crediya.model.loanaplication.loanAplication.LoanType;

public class LoanAplicationValidator {

    public static void validate(LoanAplication loanAplication) {
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
