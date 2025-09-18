package co.com.crediya.api.mapper;

import org.mapstruct.Mapper;

import co.com.crediya.api.dto.ApplyLoanRqDTO;
import co.com.crediya.api.dto.ApplyLoanRsDTO;
import co.com.crediya.model.loanapplication.LoanApplication;
import co.com.crediya.model.loanapplication.LoanApplicationStatus;

@Mapper(componentModel = "spring")
public interface LoanAplicationMapper {

    default LoanApplication toDomain(ApplyLoanRqDTO dto) {
        return new LoanApplication.Builder()
                .clientId(dto.clientId())
                .amount(dto.amount())
                .term(dto.term())
                .loanType(dto.loanType())
                .status(LoanApplicationStatus.PENDING)  
                .build();
    }

    ApplyLoanRsDTO toResponse(LoanApplication domain);
}
