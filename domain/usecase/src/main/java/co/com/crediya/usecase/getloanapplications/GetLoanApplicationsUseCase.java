package co.com.crediya.usecase.getloanapplications;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.List;
import java.util.Set;
import java.util.UUID;
import java.util.stream.Collectors;

import co.com.crediya.model.customer.Customer;
import co.com.crediya.model.customer.gateways.CustomerGateway;
import co.com.crediya.model.exceptions.BusinessException;
import co.com.crediya.model.exceptions.ErrorCode;
import co.com.crediya.model.loanapplication.LoanApplication;
import co.com.crediya.model.loanapplication.filter.LoanAplicationFilter;
import co.com.crediya.model.pagedLoanApplication.EvaluationLoanApplication;
import co.com.crediya.model.pagedLoanApplication.PageResult;
import co.com.crediya.model.pagedLoanApplication.gateways.EvaluationLoanApplicationGateway;
import reactor.core.publisher.Mono;

public class GetLoanApplicationsUseCase {

    private static final int SCALE = 2;

    private final EvaluationLoanApplicationGateway repository;
    private final CustomerGateway customerGateway;

    public GetLoanApplicationsUseCase(EvaluationLoanApplicationGateway repository, CustomerGateway customerGateway) {
        this.repository = repository;
        this.customerGateway = customerGateway;
    }

    public Mono<PageResult> findPaged(int page, int size, LoanAplicationFilter filter) {

        Mono<List<EvaluationLoanApplication>> loansMono = repository.findPaged(page, size, filter).collectList();
        Mono<Long> count = repository.count(filter);

        return Mono.zip(loansMono, count)
                .flatMap(tuple -> {
                    List<EvaluationLoanApplication> loans = tuple.getT1();
                    long totalElements = tuple.getT2();
                    int totalPages = (int) Math.ceil((double) totalElements / size);

                    if(page < totalPages ){
                        Set<UUID> clientIds = getIdFromLoanList(loans);
                        return customerGateway.findByIdList(clientIds)
                                .map(customers -> enrich(loans, customers))
                                .map(enrichedLoans -> new PageResult(
                                        enrichedLoans,
                                        totalElements,
                                        totalPages,
                                        page,
                                        size
                                ));
                    } else{
                        return Mono.error(new BusinessException(
                            ErrorCode.INVALID_ARGUMENT, 
                            "The page number you requested does not exist")
                        );
                    }
                });
    }

    private List<EvaluationLoanApplication> enrich( List<EvaluationLoanApplication> loans, Set<Customer> customers) {
        return loans.stream()
                .map(eval -> {
                    UUID clientId = eval.getLoanApplication().getClientId();
                    Customer customer = customers.stream()
                            .filter(c -> c.id().equals(clientId))
                            .findFirst()
                            .orElse(null);

                    if (customer == null) return eval;

                    return eval.toBuilder()
                            .email(customer.email())
                            .baseSalary(customer.baseSalary())
                            .monthlyAmountLoanApplication(
                                calculateMonthlyAmount(eval.getLoanApplication(), eval.getInterestRate())
                            )
                            .build();
                })
                .toList();
    }

    private BigDecimal calculateMonthlyAmount(LoanApplication loan, BigDecimal interestRate) {

        BigDecimal interest = loan.getAmount()
                                    .multiply(interestRate)
                                    .divide(BigDecimal.valueOf(100), SCALE + 2, RoundingMode.HALF_UP);

        BigDecimal totalWithInterest = loan.getAmount().add(interest);

        return totalWithInterest.divide(
                BigDecimal.valueOf(loan.getTerm()),
                SCALE,
                RoundingMode.HALF_UP
        );
    }

    private Set<UUID> getIdFromLoanList(List<EvaluationLoanApplication> loans) {
        return loans.stream()
                    .map(e -> e.getLoanApplication().getClientId())
                    .collect(Collectors.toSet());
    }
}
