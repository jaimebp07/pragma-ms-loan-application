package co.com.crediya.model.loanaplication.loanAplication.validator;

import java.math.BigDecimal;

import co.com.crediya.model.loanaplication.ecxeptions.BusinessException;
import co.com.crediya.model.loanaplication.loanAplication.LoanAplication;
import co.com.crediya.model.loanaplication.loanAplication.LoanType;

public class LoanAplicationValidator {

    public static void validate(LoanAplication loanAplication) {
        if (loanAplication.getClientId() == null || loanAplication.getClientId().isBlank()) {
            throw new BusinessException("Customer ID is required", "CLIENT_ID_REQUIRED");
        }
        if (loanAplication.getAmount() == null || loanAplication.getAmount().compareTo(BigDecimal.ZERO) <= 0) {
            throw new BusinessException("The amount must be greater than zero", "INVALID_AMOUNT");
        }
        if (loanAplication.getTerm() == null || loanAplication.getTerm() <= 0) {
            throw new BusinessException("The term must be greater than zero", "INVALID_TERM");
        }
        if (loanAplication.getLoanType() == null) {
            throw new BusinessException("The type of loan is mandatory", "LOAN_TYPE_REQUIRED");
        }

        LoanType.fromValue(loanAplication.getLoanType().name());
    }
}
