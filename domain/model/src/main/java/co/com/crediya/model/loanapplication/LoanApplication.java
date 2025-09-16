package co.com.crediya.model.loanapplication;

import java.math.BigDecimal;
import java.util.UUID;

public class LoanApplication {

    private final UUID id;
    private final UUID clientId;
    private final BigDecimal amount;
    private final Integer term;
    private final LoanType loanType;
    private final LoanApplicationStatus status;  

    private LoanApplication(Builder builder) {
        this.id = builder.id;
        this.clientId = builder.clientId;
        this.amount = builder.amount;
        this.term = builder.term;
        this.loanType = builder.loanType;
        this.status = builder.status;
    }

    public static class Builder {
        private UUID id;
        private UUID clientId;
        private BigDecimal amount;
        private Integer term;
        private LoanType loanType;
        private LoanApplicationStatus status;
        
        public Builder id(UUID id) {
            this.id = id; return this;
        }

        public Builder clientId(UUID clientId) {
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

        public Builder status(LoanApplicationStatus status) {
            this.status = status;
            return this;
        }

        public LoanApplication build() {
            return new LoanApplication(this);
        }
    }

    public Builder toBuilder() {
        return new Builder()
                .id(this.id)
                .clientId(this.clientId)
                .amount(this.amount)
                .term(this.term)
                .loanType(this.loanType)
                .status(this.status);
    } 

    public UUID getId() {
        return id;
    }

    public UUID getClientId() {
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

    public LoanApplicationStatus getStatus() {
        return status;
    }
}
