package co.com.crediya.usecase.applyloan;


import co.com.crediya.model.customer.gateways.CustomerGateway;
import co.com.crediya.model.exceptions.BusinessException;
import co.com.crediya.model.exceptions.ErrorCode;
import co.com.crediya.model.loanapplication.LoanApplication;
import co.com.crediya.model.loanapplication.LoanApplicationStatus;
import co.com.crediya.model.loanapplication.LoanType;
import co.com.crediya.model.loanapplication.gateways.LoanAplicationRepository;
import co.com.crediya.model.security.TokenServiceGateway;

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
        private CustomerGateway clientRepository;
        private TokenServiceGateway tokenServiceGateway;

        @BeforeEach
        void setUp() {
                loanAplicationRepository = Mockito.mock(LoanAplicationRepository.class);
                clientRepository = Mockito.mock(CustomerGateway.class);
                tokenServiceGateway = Mockito.mock(TokenServiceGateway.class);
                applyLoanUseCase = new ApplyLoanUseCase(loanAplicationRepository, clientRepository, tokenServiceGateway);

                when(clientRepository.existsById(any(UUID.class))).thenReturn(Mono.just(true));
        }

        private LoanApplication buildLoanAplication() {
                return new LoanApplication.Builder()
                        .id(UUID.randomUUID())
                        .clientId(UUID.randomUUID())
                        .amount(BigDecimal.valueOf(5000))
                        .term(12)
                        .loanType(LoanType.PERSONAL)
                        .status(LoanApplicationStatus.APPROVED) // será sobrescrito a PENDING
                        .build();
        }

        @Test
        void shouldApplyLoanSuccessfully() {
                LoanApplication input = buildLoanAplication();

                when(tokenServiceGateway.getAuthUserId()).thenReturn(Mono.just(input.getClientId()));
                when(loanAplicationRepository.applyLoan(any(LoanApplication.class)))
                        .thenAnswer(inv -> Mono.just(inv.getArgument(0)));

                StepVerifier.create(applyLoanUseCase.applyLoan(input))
                        .expectNextMatches(loan ->
                                loan.getStatus() == LoanApplicationStatus.PENDING &&
                                loan.getClientId().equals(input.getClientId()) &&
                                loan.getAmount().equals(BigDecimal.valueOf(5000)))
                        .verifyComplete();

                verify(loanAplicationRepository, times(1)).applyLoan(any(LoanApplication.class));
        }

        @Test
        void shouldFailWhenClientIdDoesNotMatchToken() {
                LoanApplication input = buildLoanAplication();
                UUID differentClientId = UUID.randomUUID();

                when(tokenServiceGateway.getAuthUserId()).thenReturn(Mono.just(differentClientId));

                StepVerifier.create(applyLoanUseCase.applyLoan(input))
                        .expectErrorMatches(ex ->
                                ex instanceof BusinessException &&
                                ((BusinessException) ex).getErrorCode() == ErrorCode.UNAUTHORIZED)
                        .verify();
        }

        @Test
        void shouldFailWhenClientNotFound() {
                LoanApplication input = buildLoanAplication();

                when(tokenServiceGateway.getAuthUserId()).thenReturn(Mono.just(input.getClientId()));
                when(clientRepository.existsById(input.getClientId())).thenReturn(Mono.just(false));

                StepVerifier.create(applyLoanUseCase.applyLoan(input))
                        .expectErrorMatches(ex ->
                                ex instanceof BusinessException &&
                                ((BusinessException) ex).getErrorCode() == ErrorCode.CLIENT_NOT_FOUND)
                        .verify();
        }

        @Test
        void shouldMapDatabaseErrorToBusinessException() {
                LoanApplication input = buildLoanAplication();

                 when(tokenServiceGateway.getAuthUserId()).thenReturn(Mono.just(input.getClientId()));
                when(loanAplicationRepository.applyLoan(any(LoanApplication.class)))
                        .thenReturn(Mono.error(new RuntimeException("R2DBC connection failed")));

                StepVerifier.create(applyLoanUseCase.applyLoan(input))
                        .expectErrorMatches(ex ->
                                ex instanceof BusinessException &&
                                ((BusinessException) ex).getErrorCode() == ErrorCode.DB_ERROR)
                        .verify();

                verify(loanAplicationRepository, times(1)).applyLoan(any(LoanApplication.class));
        }

        @Test
        void shouldMapUnexpectedErrorToBusinessException() {
                LoanApplication input = buildLoanAplication();

                when(loanAplicationRepository.applyLoan(any(LoanApplication.class)))
                        .thenReturn(Mono.error(new RuntimeException("Some other error")));
                when(tokenServiceGateway.getAuthUserId()).thenReturn(Mono.just(input.getClientId()));

                System.out.println("----------> input "+input.getAmount());
                StepVerifier.create(applyLoanUseCase.applyLoan(input))
                        .expectErrorMatches(ex ->
                                ex instanceof BusinessException &&
                                ((BusinessException) ex).getErrorCode() == ErrorCode.UNEXPECTED_ERROR)
                        .verify();

                verify(loanAplicationRepository, times(1)).applyLoan(any(LoanApplication.class));
        }
}
