package co.com.crediya.api;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

import org.springframework.stereotype.Component;
import org.springframework.web.reactive.function.server.ServerRequest;
import org.springframework.web.reactive.function.server.ServerResponse;

import co.com.crediya.api.dto.ApplyLoanRqDTO;
import co.com.crediya.api.dto.UpdateStatusRqDTO;
import co.com.crediya.api.mapper.LoanAplicationMapper;
import co.com.crediya.api.mapper.PageResultMapper;
import co.com.crediya.model.exceptions.BusinessException;
import co.com.crediya.model.loanapplication.LoanApplicationStatus;
import co.com.crediya.model.loanapplication.LoanType;
import co.com.crediya.model.loanapplication.filter.LoanAplicationFilter;
import co.com.crediya.usecase.applyloan.ApplyLoanUseCase;
import co.com.crediya.usecase.getloanapplications.GetLoanApplicationsUseCase;
import co.com.crediya.usecase.updateloanapplicationstatus.UpdateLoanApplicationStatusUseCase;

import java.time.Instant;
import java.util.Optional;

import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.security.access.prepost.PreAuthorize;

import reactor.core.publisher.Mono;

@Slf4j
@Component
@RequiredArgsConstructor
public class HandlerV1 {

    private  final ApplyLoanUseCase applyLoanUseCase;
    private final LoanAplicationMapper loanAplicationMapper;
    private final GetLoanApplicationsUseCase getLoanApplicationsUseCase;
    private final PageResultMapper pageResultMapper;
    private final UpdateLoanApplicationStatusUseCase updateLoanApplicationStatusUseCase;

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

    @PreAuthorize("hasRole('ADVISOR')")
    public Mono<ServerResponse> getLoanApplications(ServerRequest serverRequest){

        int page = Integer.parseInt(serverRequest.queryParam("page").orElse("0"));
        int size = Integer.parseInt(serverRequest.queryParam("size").orElse("20"));
        String status = serverRequest.queryParam("status").orElse(null);
        String loanType = serverRequest.queryParam("loanType").orElse(null);

        LoanAplicationFilter filter;
        try {
                filter = new LoanAplicationFilter(
                        Optional.ofNullable(status).map(LoanApplicationStatus::fromValue),
                        Optional.ofNullable(loanType).map(LoanType::fromValue)
                );
        } catch (BusinessException ex) {
                log.warn("Invalid filter: ", ex);
                return ServerResponse.status(HttpStatus.BAD_REQUEST)
                        .contentType(MediaType.APPLICATION_JSON)
                        .bodyValue(new ErrorResponse("BUSINESS_ERROR", ex.getMessage()));
        }

        return getLoanApplicationsUseCase.findPaged(page, size, filter)
                .doOnNext(dto -> log.info("Loan Aplication, Request received: {}", dto))
                .map(pageResultMapper::toDTO)
                .flatMap(pageResultRs -> ServerResponse.ok().bodyValue(pageResultRs))
                .doOnSuccess(resp -> log.info("The page pagination was successfully obtained."))
                .doOnError(error -> log.error("Paged Loan Aplication, Error occurred: {}", error.getMessage()))
                .onErrorResume(BusinessException.class, ex -> {
                        log.warn("Pagination Loan Aplication, Business error: {}", ex.getMessage());
                        return ServerResponse.status(HttpStatus.BAD_REQUEST)
                                .contentType(MediaType.APPLICATION_JSON)
                                .bodyValue(new ErrorResponse("BUSINESS_ERROR", ex.getMessage()));
                        }
                )
                .onErrorResume(Exception.class, ex -> {
                        log.error("Pagination Loan Aplication, Unexpected error: ", ex);
                        return ServerResponse.status(HttpStatus.INTERNAL_SERVER_ERROR)
                                .contentType(MediaType.APPLICATION_JSON)
                                .bodyValue(new ErrorResponse("INTERNAL_ERROR", ex.getMessage()));
                        }
                );
    }

    @PreAuthorize("hasRole('ADVISOR')")
    public Mono<ServerResponse>updateLoanAplicationStatus(ServerRequest serverRequest){
        return serverRequest.bodyToMono(UpdateStatusRqDTO.class)
        .flatMap(dto -> {
            //LoanApplicationStatus status = LoanApplicationStatus.fromValue(dto.status());
            return updateLoanApplicationStatusUseCase
                .updateLoanAplicationStatus(dto.loanId(), dto.status(), Optional.ofNullable(dto.comment()))
                .flatMap(updated -> {
                    // Publicar evento SQS
                    /*LoanDecisionEvent event = new LoanDecisionEvent(
                        updated.getLoanApplication().getId(),
                        updated.getEmail(),
                        status.getLoanApplication().getStatus(),
                        "NOMBRE",
                        Instant.now()
                    );*/
                    //return sqsPublisher.publish(event).thenReturn(updated);
                    return Mono.empty();
                });
        })
        .flatMap(updated -> ServerResponse.ok().bodyValue(updated))
        .doOnError(e -> log.error("Error updating status", e))
        .onErrorResume(e -> ServerResponse.badRequest()
                .bodyValue(new ErrorResponse("ERROR", e.getMessage()))
        );
    }

    private record ErrorResponse(String code, String message) {}
}
