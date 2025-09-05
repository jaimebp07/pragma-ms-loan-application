package co.com.crediya.api;

import static org.mockito.Mockito.when;

import java.math.BigDecimal;
import java.util.UUID;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.ImportAutoConfiguration;
import org.springframework.boot.test.autoconfigure.web.reactive.WebFluxTest;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.reactive.server.WebTestClient;

import co.com.crediya.api.dto.ApplyLoanRqDTO;
import co.com.crediya.api.mapper.LoanAplicationMapper;
import co.com.crediya.model.loanaplication.loanAplication.LoanAplication;
import co.com.crediya.model.loanaplication.loanAplication.LoanType;
import co.com.crediya.usecase.applyloan.ApplyLoanUseCase;
import reactor.core.publisher.Mono;

@ContextConfiguration(classes = {RouterRest.class, HandlerV1.class, HandlerV2.class})
@WebFluxTest
@ImportAutoConfiguration(exclude = {
        org.springframework.boot.autoconfigure.security.reactive.ReactiveSecurityAutoConfiguration.class,
        org.springframework.boot.autoconfigure.security.oauth2.resource.reactive.ReactiveOAuth2ResourceServerAutoConfiguration.class
})
class RouterRestTest {

    @Autowired
    private WebTestClient webTestClient;

    @MockitoBean
    private ApplyLoanUseCase applyLoanUseCase;

    @MockitoBean
    private LoanAplicationMapper loanAplicationMapper;

    @Test
    void testApplyLoanSuccess() {

        ApplyLoanRqDTO requestDto = new ApplyLoanRqDTO(UUID.fromString("47d3808b-fdc3-4d56-84f3-48691fecbd10"), 
                java.math.BigDecimal.valueOf(1000), 12, null);

        LoanAplication domain = new LoanAplication.Builder()
        .id(UUID.randomUUID())
        .clientId(UUID.fromString("47d3808b-fdc3-4d56-84f3-48691fecbd10"))
        .amount(BigDecimal.valueOf(1000))
        .term(12)
        .loanType(LoanType.AUTO)
        .build();
        ApplyLoanRqDTO responseDto = requestDto;

        when(loanAplicationMapper.toDomain(requestDto)).thenReturn(domain);
        when(applyLoanUseCase.applyLoan(domain)).thenReturn(Mono.just(domain));
        when(loanAplicationMapper.toDTO(domain)).thenReturn(responseDto);

        
        webTestClient.post()
                .uri("/api/v1/solicitud")
                .bodyValue(requestDto)
                .exchange()
                .expectStatus().isCreated()
                .expectBody(ApplyLoanRqDTO.class)
                .isEqualTo(responseDto);
    }
}
