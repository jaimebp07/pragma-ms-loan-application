package co.com.crediya.api.dto;

import java.util.List;

import co.com.crediya.model.pagedLoanApplication.EvaluationLoanApplication;

public record PageResultLoanApplicationRsDTO ( 
    List<EvaluationLoanApplication> content,
    long totalElements,
    int totalPages,
    int currentPage,
    int pageSize
) { }
