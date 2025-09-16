package co.com.crediya.r2dbc.entity;

import java.math.BigDecimal;
import java.util.UUID;

import org.springframework.data.annotation.Id;
import org.springframework.data.relational.core.mapping.Column;
import org.springframework.data.relational.core.mapping.Table;

import co.com.crediya.model.loanapplication.LoanApplicationStatus;
import lombok.Data;

@Data
@Table("credi_ya.loan_applications")
public class LoanAplicationEntity {

    @Id
    @Column("id")
    private UUID id;

    @Column("client_id")
    private UUID clientId;

    @Column("amount")
    private BigDecimal amount;

    @Column("term")
    private Integer term;
    
    @Column("loan_type_id")
    private UUID loanType;

    @Column("status")
    private LoanApplicationStatus status;
}
