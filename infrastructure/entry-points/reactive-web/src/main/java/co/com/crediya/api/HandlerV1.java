package co.com.crediya.api;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

import org.springframework.stereotype.Component;
import org.springframework.web.reactive.function.server.ServerRequest;
import org.springframework.web.reactive.function.server.ServerResponse;

import co.com.crediya.api.dto.ApplyLoanRqDTO;
import co.com.crediya.api.mapper.LoanAplicationMapper;
import co.com.crediya.model.loanaplication.ecxeptions.BusinessException;
import co.com.crediya.usecase.applyloan.ApplyLoanUseCase;

import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.security.access.prepost.PreAuthorize;

//import org.springframework.security.access.prepost.PreAuthorize;
import reactor.core.publisher.Mono;

@Slf4j
@Component
@RequiredArgsConstructor
public class HandlerV1 {

    private  final ApplyLoanUseCase applyLoanUseCase;
    private final LoanAplicationMapper loanAplicationMapper;

    @PreAuthorize("hasRole('CLIENT')")
    public Mono<ServerResponse> applyLoan(ServerRequest serverRequest) {
        return serverRequest.bodyToMono(ApplyLoanRqDTO.class)
        .doOnNext(dto -> log.info("Loan Aplication, Request received: {}", dto))
        .map(loanAplicationMapper::toDomain)
        .flatMap(applyLoanUseCase::applyLoan)
        .map(loanAplicationMapper::toResponse)
        .doOnNext(dto -> log.info("Loan Aplication, Domain processed: {}", dto))
        .flatMap(applyLoanRsDTO -> ServerResponse
            .status(HttpStatus.CREATED)
            .bodyValue(applyLoanRsDTO)
        )
        .doOnSuccess(resp -> log.info("Loan Aplication, Response  created successfully"))
        .doOnError(error -> log.error("Loan Aplication, Error occurred: {}", error.getMessage()))
        .onErrorResume(BusinessException.class, ex -> {
                log.warn("Loan Aplication, Business error: {}", ex.getMessage());
                return ServerResponse.status(HttpStatus.BAD_REQUEST)
                        .contentType(MediaType.APPLICATION_JSON)
                        .bodyValue(new ErrorResponse("BUSINESS_ERROR", ex.getMessage()));
                }
        )
        .onErrorResume(Exception.class, ex -> {
                log.error("Loan Aplication, Unexpected error: ", ex);
                return ServerResponse.status(HttpStatus.INTERNAL_SERVER_ERROR)
                        .contentType(MediaType.APPLICATION_JSON)
                        .bodyValue(new ErrorResponse("INTERNAL_ERROR", ex.getMessage()));
                }
        );
    }

    private record ErrorResponse(String code, String message) {}
}
