package co.com.crediya.api.mapper;

import org.mapstruct.Mapper;

import co.com.crediya.api.dto.LoanAplicationDTO;
import co.com.crediya.model.loanaplication.loanAplication.LoanAplication;
import co.com.crediya.model.loanaplication.loanAplication.LoanAplicationStatus;

@Mapper(componentModel = "spring")
public interface LoanAplicationMapper {

    LoanAplicationDTO toDTO(LoanAplication domain);

    default LoanAplication toDomain(LoanAplicationDTO dto) {
        return new LoanAplication.Builder()
                .clientId(dto.clientId())
                .amount(dto.amount())
                .term(dto.term())
                .loanType(dto.loanType())
                .status(LoanAplicationStatus.PENDING)  
                .build();
    }
}
