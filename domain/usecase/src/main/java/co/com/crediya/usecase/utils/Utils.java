package co.com.crediya.usecase.utils;

import java.math.BigDecimal;
import java.math.RoundingMode;

import co.com.crediya.model.loanapplication.LoanApplication;

public class  Utils {

    private static final int SCALE = 2;

    public static BigDecimal calculateMonthlyAmount(LoanApplication loan, BigDecimal interestRate) {

        BigDecimal interest = loan.getAmount()
                                    .multiply(interestRate)
                                    .divide(BigDecimal.valueOf(100), SCALE + 2, RoundingMode.HALF_UP);

        BigDecimal totalWithInterest = loan.getAmount().add(interest);

        return totalWithInterest.divide(
                BigDecimal.valueOf(loan.getTerm()),
                SCALE,
                RoundingMode.HALF_UP
        );
    }
}
