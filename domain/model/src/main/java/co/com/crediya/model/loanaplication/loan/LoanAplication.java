package co.com.crediya.model.loanaplication.loan;

import java.math.BigDecimal;

public class LoanAplication {

    private final String clientId;
    private final BigDecimal amount;
    private final Integer term;
    private final LoanType loanType;

    private LoanAplication(Builder builder) {
        this.clientId = builder.clientId;
        this.amount = builder.amount;
        this.term = builder.term;
        this.loanType = builder.loanType;
    }

    public static class Builder {
        private String clientId;
        private BigDecimal amount;
        private Integer term;
        private LoanType loanType;
        
        public Builder clientId(String clientId) {
            this.clientId = clientId;
            return this;
        }

        public Builder amount(BigDecimal amount) {
            this.amount = amount;
            return this;
        }
        
        public Builder term(Integer term) {
            this.term = term;
            return this;
        }

        public Builder loanType(LoanType loanType) {
            this.loanType = loanType;
            return this;
        }

        public LoanAplication build() {
            return new LoanAplication(this);
        }
    }

    public Builder toBuilder() {
        return new Builder()
                .clientId(this.clientId)
                .amount(this.amount)
                .term(this.term)
                .loanType(this.loanType);
    } 
    
    public static LoanType fromString(String value) {
        for (LoanType type : LoanType.values()) {
            if (type.name().equalsIgnoreCase(value) || type.getDescription().equalsIgnoreCase(value)) {
                return type;
            }
        }
        throw new IllegalArgumentException("No enum constant for value: " + value);
    }

    public String getClientId() {
        return clientId;
    }

    public BigDecimal getAmount() {
        return amount;
    }

    public Integer getTerm() {
        return term;
    }

    public LoanType getLoanType() {
        return loanType;
    }
}
