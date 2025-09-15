package co.com.crediya.api;


import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.reactive.function.server.RouterFunction;
import org.springframework.web.reactive.function.server.RouterFunctions;
import org.springframework.web.reactive.function.server.ServerResponse;

import co.com.crediya.api.dto.ApplyLoanRqDTO;
import co.com.crediya.api.dto.ApplyLoanRsDTO;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;

import org.springdoc.core.annotations.RouterOperation;
import org.springdoc.core.annotations.RouterOperations;

@Configuration
public class RouterRest {

    @Bean
    @RouterOperations({
        @RouterOperation(
            path = "/api/v1/solicitud",
            produces = { "application/json" },
            consumes = { "application/json" },
            beanClass = HandlerV1.class,
            beanMethod = "applyLoan",
            operation = @Operation(
                operationId = "applyLoan",
                summary = "Apply for a loan",
                description = "Register a new loan application in the system",
                tags = { "Loan Application V1" },
                security = { @io.swagger.v3.oas.annotations.security.SecurityRequirement(name = "bearerAuth") },
                requestBody = @io.swagger.v3.oas.annotations.parameters.RequestBody(
                    required = true,
                    description = "Loan application details",
                    content = @Content(
                        mediaType = "application/json",
                        schema = @Schema(implementation = ApplyLoanRqDTO.class)
                    )
                ),
                responses = {
                    @ApiResponse(
                        responseCode = "201",
                        description = "Request created successfully",
                        content = @Content(schema = @Schema(implementation = ApplyLoanRsDTO.class))
                    ),
                    @ApiResponse(
                        responseCode = "400",
                        description = "Validation error",
                        content = @Content(schema = @Schema(implementation = String.class))
                    ),
                    @ApiResponse(
                        responseCode = "401",
                        description = "Unauthorized, invalid or missing token",
                        content = @Content(
                            schema = @Schema(example = "{\"code\":\"UNAUTHORIZED\", \"message\":\"Invalid or missing token\"}")
                        )
                    ),
                    @ApiResponse(
                        responseCode = "500",
                        description = "Internal error",
                        content = @Content(schema = @Schema(implementation = String.class))
                    )
                }
            )
        )
    })
    RouterFunction<ServerResponse> routerFunction(HandlerV1 handlerV1, HandlerV2 handlerV2) {
        return RouterFunctions
            .route()
            .path("/api/v1", builder -> builder
                .POST("/solicitud", handlerV1::applyLoan)
                .GET("/solicitud", handlerV1::getLoanApplications)
            )
            .path("/api/v2", builder -> builder
                .POST("/usecase/otherpath", handlerV2::listenPOSTUseCase))
            .build();
    }
}
