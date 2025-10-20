package co.com.crediya.api;

import co.com.crediya.api.dto.ApplyLoanRqDTO;
import co.com.crediya.api.dto.ApplyLoanRsDTO;
import co.com.crediya.api.mapper.LoanAplicationMapper;
import co.com.crediya.api.mapper.PageResultMapper;
import co.com.crediya.model.exceptions.BusinessException;
import co.com.crediya.model.exceptions.ErrorCode;
import co.com.crediya.model.loanapplication.LoanApplication;
import co.com.crediya.model.loanapplication.LoanApplicationStatus;
import co.com.crediya.model.loanapplication.LoanType;
import co.com.crediya.usecase.applyloan.ApplyLoanUseCase;
import co.com.crediya.usecase.getloanapplications.GetLoanApplicationsUseCase;
import co.com.crediya.usecase.updateloanapplicationstatus.UpdateLoanApplicationStatusUseCase;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.test.web.reactive.server.WebTestClient;
import org.springframework.web.reactive.function.server.RouterFunction;
import org.springframework.web.reactive.function.server.RouterFunctions;
import org.springframework.web.reactive.function.server.ServerResponse;
import reactor.core.publisher.Mono;

import java.math.BigDecimal;
import java.net.URI;
import java.util.UUID;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

class HandlerV1Test {

    private ApplyLoanUseCase applyLoanUseCase;
    private LoanAplicationMapper loanAplicationMapper;
    private WebTestClient webTestClient;
    private GetLoanApplicationsUseCase getLoanApplicationsUseCase;
    private PageResultMapper pageResultMapper;
    private UpdateLoanApplicationStatusUseCase updateLoanApplicationStatusUseCase;

    @BeforeEach
    void setUp() {
        applyLoanUseCase = mock(ApplyLoanUseCase.class);
        loanAplicationMapper = mock(LoanAplicationMapper.class);
        pageResultMapper = mock(PageResultMapper.class);

        HandlerV1 handlerV1 = new HandlerV1(applyLoanUseCase, loanAplicationMapper,getLoanApplicationsUseCase, pageResultMapper, updateLoanApplicationStatusUseCase);

        RouterFunction<ServerResponse> router = RouterFunctions.route()
                .POST("/api/v1/solicitud", handlerV1::applyLoan)
                .build();

        webTestClient = WebTestClient.bindToRouterFunction(router).build();
    }

    @Test
    void testApplyLoanSuccess() {
        UUID expectedLoanId = UUID.randomUUID();

        ApplyLoanRqDTO requestDto = new ApplyLoanRqDTO(
                UUID.randomUUID(),
                BigDecimal.valueOf(1000),
                12,
                LoanType.PERSONAL
        );

        LoanApplication loanAplication = new LoanApplication.Builder()
                .clientId(expectedLoanId)
                .amount(requestDto.amount())
                .term(requestDto.term())
                .loanType(requestDto.loanType())
                .status(LoanApplicationStatus.PENDING)
                .build();

        ApplyLoanRsDTO responseDto = new ApplyLoanRsDTO(
                expectedLoanId,
                requestDto.clientId(),
                requestDto.amount(),
                requestDto.term(),
                requestDto.loanType().toString(),
                loanAplication.getStatus()
        );

        when(loanAplicationMapper.toDomain(requestDto)).thenReturn(loanAplication);
        when(applyLoanUseCase.applyLoan(any())).thenReturn(Mono.just(loanAplication));
        when(loanAplicationMapper.toResponse(loanAplication)).thenReturn(responseDto);

        // Act & Assert
        webTestClient.post()
                .uri(URI.create("/api/v1/solicitud"))
                .bodyValue(requestDto)
                .exchange()
                .expectStatus().isCreated()
                .expectBody(ApplyLoanRsDTO.class)
                .isEqualTo(responseDto);

        verify(applyLoanUseCase, times(1)).applyLoan(any());
    }

    @Test
    void testApplyLoanBusinessException() {
        // Arrange
        ApplyLoanRqDTO requestDto = new ApplyLoanRqDTO(
                UUID.randomUUID(),
                BigDecimal.valueOf(500),
                6,
                LoanType.PERSONAL
        );

        LoanApplication loanAplication = new LoanApplication.Builder()
                .clientId(requestDto.clientId())
                .amount(requestDto.amount())
                .term(requestDto.term())
                .loanType(requestDto.loanType())
                .build();

        when(loanAplicationMapper.toDomain(requestDto)).thenReturn(loanAplication);
        when(applyLoanUseCase.applyLoan(any()))
                .thenReturn(Mono.error(new BusinessException(ErrorCode.CLIENT_NOT_FOUND, "Client not found")));

        // Act & Assert
        webTestClient.post()
                .uri("/api/v1/solicitud")
                .bodyValue(requestDto)
                .exchange()
                .expectStatus().isBadRequest()
                .expectBody()
                .jsonPath("$.code").isEqualTo("BUSINESS_ERROR")
                .jsonPath("$.message").isEqualTo("Client not found");
    }

    @Test
    void testApplyLoanUnexpectedError() {
        // Arrange
        ApplyLoanRqDTO requestDto = new ApplyLoanRqDTO(
                UUID.randomUUID(),
                BigDecimal.valueOf(500),
                6,
                LoanType.PERSONAL
        );

        LoanApplication loanAplication = new LoanApplication.Builder()
                .clientId(requestDto.clientId())
                .amount(requestDto.amount())
                .term(requestDto.term())
                .loanType(requestDto.loanType())
                .build();

        when(loanAplicationMapper.toDomain(requestDto)).thenReturn(loanAplication);
        when(applyLoanUseCase.applyLoan(any()))
                .thenReturn(Mono.error(new RuntimeException("DB exploded")));

        // Act & Assert
        webTestClient.post()
                .uri("/api/v1/solicitud")
                .bodyValue(requestDto)
                .exchange()
                .expectStatus().is5xxServerError()
                .expectBody()
                .jsonPath("$.code").isEqualTo("INTERNAL_ERROR")
                .jsonPath("$.message").isEqualTo("DB exploded");
    }
}
