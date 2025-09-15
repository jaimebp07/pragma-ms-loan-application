package co.com.crediya.usecase.getloanapplications;

import java.util.List;

import co.com.crediya.model.loanaplication.filter.LoanAplicationFilter;
import co.com.crediya.model.loanaplication.gateways.LoanAplicationRepository;
import co.com.crediya.model.loanaplication.loanAplication.LoanAplication;
import co.com.crediya.usecase.common.PageResult;
import reactor.core.publisher.Mono;

public class GetLoanApplicationsUseCase {

    private final LoanAplicationRepository repository;

    public GetLoanApplicationsUseCase(LoanAplicationRepository repository) {
        this.repository = repository;
    }

    public Mono<PageResult<LoanAplication>> findPaged(int page, int size, LoanAplicationFilter filter) {

        Mono<List<LoanAplication>> contentMono = repository.findPaged(page, size, filter).collectList();
        Mono<Long> count = repository.count(filter);

        return Mono.zip(contentMono, count)
                .map(tuple -> {
                    List<LoanAplication> content = tuple.getT1();
                    long totalElements = tuple.getT2();
                    int totalPages = (int) Math.ceil((double) totalElements / size);

                    return new PageResult<LoanAplication>(
                        content,
                        totalElements,
                        totalPages,
                        page,
                        size
                    );
                });
    }
}
