package co.com.crediya.api.mapper;

import org.mapstruct.Mapper;

import co.com.crediya.api.dto.PageResultLoanApplicationRsDTO;
import co.com.crediya.model.pagedLoanApplication.PageResult;

@Mapper(componentModel = "spring")
public interface PageResultMapper {

    PageResultLoanApplicationRsDTO toDTO(PageResult domain);

}
