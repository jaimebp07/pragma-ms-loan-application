package co.com.crediya.model.pagedLoanApplication;

import java.math.BigDecimal;

import co.com.crediya.model.loanapplication.LoanApplication;

public class EvaluationLoanApplication {

    private final LoanApplication loanApplication;
    private final BigDecimal interestRate;
    private final String email;
    private final BigDecimal baseSalary;
    private final BigDecimal monthlyAmountLoanApplication;

    private EvaluationLoanApplication(Builder builder) {
        this.loanApplication = builder.loanApplication;
        this.interestRate = builder.interestRate;
        this.email = builder.email;
        this.baseSalary = builder.baseSalary;
        this.monthlyAmountLoanApplication = builder.monthlyAmountLoanApplication;
    }

    public static class Builder {
        private LoanApplication loanApplication;
        private BigDecimal interestRate;
        private String email;
        private BigDecimal baseSalary;
        private BigDecimal monthlyAmountLoanApplication;

        public Builder loanApplication(LoanApplication loanApplication) {
            this.loanApplication = loanApplication;
            return this;
        }

        public Builder interestRate(BigDecimal interestRate) {
            this.interestRate = interestRate;
            return this;
        }

        public Builder email(String email) {
            this.email = email;
            return this;
        }

        public Builder baseSalary(BigDecimal baseSalary) {
            this.baseSalary = baseSalary;
            return this;
        }

        public Builder monthlyAmountLoanApplication(BigDecimal monthlyAmountLoanApplication) {
            this.monthlyAmountLoanApplication = monthlyAmountLoanApplication;
            return this;
        }

        public EvaluationLoanApplication build() {
            return new EvaluationLoanApplication(this);
        }
    }

    public Builder toBuilder() {
        return new Builder()
                .loanApplication(this.loanApplication)
                .interestRate(this.interestRate)
                .email(this.email)
                .baseSalary(this.baseSalary)
                .monthlyAmountLoanApplication(this.monthlyAmountLoanApplication);
    }

    public LoanApplication getLoanApplication() {
        return loanApplication;
    }

    public BigDecimal getInterestRate() {
        return interestRate;
    }

    public String getEmail() {
        return email;
    }

    public BigDecimal getBaseSalary() {
        return baseSalary;
    }

    public BigDecimal getMonthlyAmountLoanApplication() {
        return monthlyAmountLoanApplication;
    }
 }
