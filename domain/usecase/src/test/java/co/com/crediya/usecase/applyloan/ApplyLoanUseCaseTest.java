package co.com.crediya.usecase.applyloan;


import co.com.crediya.model.loanaplication.ecxeptions.BusinessException;
import co.com.crediya.model.loanaplication.gateways.LoanAplicationRepository;
import co.com.crediya.model.loanaplication.loanAplication.LoanAplication;
import co.com.crediya.model.loanaplication.loanAplication.LoanAplicationStatus;
import co.com.crediya.model.loanaplication.loanAplication.LoanType;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import reactor.core.publisher.Mono;
import reactor.test.StepVerifier;

import java.math.BigDecimal;
import java.util.UUID;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

public class ApplyLoanUseCaseTest {
    
    private LoanAplicationRepository loanAplicationRepository;
    private ApplyLoanUseCase applyLoanUseCase;

    @BeforeEach
    void setUp() {
        loanAplicationRepository = Mockito.mock(LoanAplicationRepository.class);
        applyLoanUseCase = new ApplyLoanUseCase(loanAplicationRepository);
    }

    private LoanAplication buildLoanAplication() {
        return new LoanAplication.Builder()
                .id(UUID.randomUUID())
                .clientId("client-123")
                .amount(BigDecimal.valueOf(5000))
                .term(12)
                .loanType(LoanType.PERSONAL)
                .status(LoanAplicationStatus.APPROVED) // será sobrescrito a PENDING
                .build();
    }

    @Test
    void shouldApplyLoanSuccessfully() {
        LoanAplication input = buildLoanAplication();

        when(loanAplicationRepository.applyLoan(any(LoanAplication.class)))
                .thenAnswer(inv -> Mono.just(inv.getArgument(0)));

        StepVerifier.create(applyLoanUseCase.applyLoan(input))
                .expectNextMatches(loan ->
                        loan.getStatus() == LoanAplicationStatus.PENDING &&
                        loan.getClientId().equals("client-123") &&
                        loan.getAmount().equals(BigDecimal.valueOf(5000)))
                .verifyComplete();

        verify(loanAplicationRepository, times(1)).applyLoan(any(LoanAplication.class));
    }

    @Test
    void shouldMapDatabaseErrorToBusinessException() {
        LoanAplication input = buildLoanAplication();

        when(loanAplicationRepository.applyLoan(any(LoanAplication.class)))
                .thenReturn(Mono.error(new RuntimeException("R2DBC connection failed")));

        StepVerifier.create(applyLoanUseCase.applyLoan(input))
                .expectErrorMatches(ex ->
                        ex instanceof BusinessException &&
                        ((BusinessException) ex).getErrorCode().equals("DB_CONNECTION_ERROR"))
                .verify();

        verify(loanAplicationRepository, times(1)).applyLoan(any(LoanAplication.class));
    }

    @Test
    void shouldMapUnexpectedErrorToBusinessException() {
        LoanAplication input = buildLoanAplication();

        when(loanAplicationRepository.applyLoan(any(LoanAplication.class)))
                .thenReturn(Mono.error(new RuntimeException("Some other error")));

        StepVerifier.create(applyLoanUseCase.applyLoan(input))
                .expectErrorMatches(ex ->
                        ex instanceof BusinessException &&
                        ((BusinessException) ex).getErrorCode().equals("UNEXPECTED_ERROR"))
                .verify();

        verify(loanAplicationRepository, times(1)).applyLoan(any(LoanAplication.class));
    }
}
