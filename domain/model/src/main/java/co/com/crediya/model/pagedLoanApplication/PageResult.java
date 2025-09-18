package co.com.crediya.model.pagedLoanApplication;

import java.util.List;

public record PageResult (
    List<EvaluationLoanApplication> content,
    long totalElements,
    int totalPages,
    int currentPage,
    int pageSize
) { }
