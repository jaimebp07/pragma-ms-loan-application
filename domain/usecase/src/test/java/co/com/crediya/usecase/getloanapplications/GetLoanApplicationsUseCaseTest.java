package co.com.crediya.usecase.getloanapplications;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.verifyNoInteractions;
import static org.mockito.Mockito.when;

import java.math.BigDecimal;
import java.util.*;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import co.com.crediya.model.customer.Customer;
import co.com.crediya.model.customer.gateways.CustomerGateway;
import co.com.crediya.model.exceptions.BusinessException;
import co.com.crediya.model.exceptions.ErrorCode;
import co.com.crediya.model.loanapplication.LoanApplication;
import co.com.crediya.model.loanapplication.LoanApplicationStatus;
import co.com.crediya.model.loanapplication.LoanType;
import co.com.crediya.model.loanapplication.filter.LoanAplicationFilter;
import co.com.crediya.model.pagedLoanApplication.EvaluationLoanApplication;
import co.com.crediya.model.pagedLoanApplication.gateways.EvaluationLoanApplicationGateway;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;
import reactor.test.StepVerifier;

public class GetLoanApplicationsUseCaseTest {

    private EvaluationLoanApplicationGateway evaluationGateway;
    private CustomerGateway customerGateway;
    private GetLoanApplicationsUseCase useCase;

    private final UUID clientId = UUID.randomUUID();

    @BeforeEach
    void setup() {
        evaluationGateway = mock(EvaluationLoanApplicationGateway.class);
        customerGateway = mock(CustomerGateway.class);
        useCase = new GetLoanApplicationsUseCase(evaluationGateway, customerGateway);
    }

    @Test
    void findPaged_success_enrichesCustomerData() {
        LoanApplication loan = new LoanApplication.Builder()
                .id(UUID.randomUUID())
                .clientId(clientId)
                .amount(BigDecimal.valueOf(1000))
                .term(10)
                .loanType(LoanType.PERSONAL)
                .status(LoanApplicationStatus.APPROVED)
                .build();

        EvaluationLoanApplication eval = new EvaluationLoanApplication.Builder()
                .loanApplication(loan)
                .interestRate(BigDecimal.valueOf(10))
                .build();

        when(evaluationGateway.findPaged(eq(0), eq(5), any()))
                .thenReturn(Flux.just(eval));
        when(evaluationGateway.count(any()))
                .thenReturn(Mono.just(1L));

        Customer customer = new Customer(
                clientId,
                "John",
                "Doe",
                "john.doe@mail.com",
                BigDecimal.valueOf(2000)
        );
        when(customerGateway.findByIdList(Set.of(clientId)))
                .thenReturn(Mono.just(Set.of(customer)));

        LoanAplicationFilter filter = new LoanAplicationFilter(Optional.empty(), Optional.empty(), Optional.empty());

        StepVerifier.create(useCase.findPaged(0, 5, filter))
                .assertNext(result -> {
                    assert result.totalElements() == 1;
                    assert result.totalPages() == 1;
                    assert result.currentPage() == 0;
                    assert result.pageSize() == 5;

                    EvaluationLoanApplication enriched = result.content().get(0);
                    assert enriched.getEmail().equals("john.doe@mail.com");
                    assert enriched.getBaseSalary().equals(BigDecimal.valueOf(2000));
                    BigDecimal monthly = enriched.getMonthlyAmountLoanApplication();
                    assert monthly != null && monthly.compareTo(BigDecimal.ZERO) > 0;
                })
                .verifyComplete();

        verify(evaluationGateway).findPaged(0, 5, filter);
        verify(customerGateway).findByIdList(Set.of(clientId));
    }

    @Test
    void findPaged_pageOutOfRange_returnsBusinessException() {
        when(evaluationGateway.findPaged(eq(1), eq(5), any()))
                .thenReturn(Flux.empty());
        when(evaluationGateway.count(any()))
                .thenReturn(Mono.just(0L));

        LoanAplicationFilter filter = new LoanAplicationFilter(Optional.empty(), Optional.empty(), Optional.empty());

        StepVerifier.create(useCase.findPaged(1, 5, filter))
                .expectErrorSatisfies(err -> {
                    assert err instanceof BusinessException;
                    BusinessException be = (BusinessException) err;
                    assert be.getErrorCode() == ErrorCode.INVALID_ARGUMENT;
                    assert be.getMessage().contains("does not exist");
                })
                .verify();

        verify(evaluationGateway).count(filter);
        verifyNoInteractions(customerGateway);
    }

    @Test
    void findPaged_customerNotFound_stillReturnsLoanWithoutEnrichment() {
        LoanApplication loan = new LoanApplication.Builder()
                .id(UUID.randomUUID())
                .clientId(clientId)
                .amount(BigDecimal.valueOf(500))
                .term(5)
                .loanType(LoanType.AUTO)
                .status(LoanApplicationStatus.PENDING)
                .build();

        EvaluationLoanApplication eval = new EvaluationLoanApplication.Builder()
                .loanApplication(loan)
                .interestRate(BigDecimal.valueOf(5))
                .build();

        when(evaluationGateway.findPaged(eq(0), eq(5), any()))
                .thenReturn(Flux.just(eval));
        when(evaluationGateway.count(any()))
                .thenReturn(Mono.just(1L));
        when(customerGateway.findByIdList(Set.of(clientId)))
                .thenReturn(Mono.just(Set.of()));

        LoanAplicationFilter filter = new LoanAplicationFilter(Optional.empty(), Optional.empty(), Optional.empty());

        StepVerifier.create(useCase.findPaged(0, 5, filter))
                .assertNext(result -> {
                    EvaluationLoanApplication loanResult = result.content().get(0);
                    assert loanResult.getEmail() == null;
                    assert loanResult.getBaseSalary() == null;
                })
                .verifyComplete();
    }
}
