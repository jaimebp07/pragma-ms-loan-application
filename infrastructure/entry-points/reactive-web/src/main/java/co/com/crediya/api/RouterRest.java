package co.com.crediya.api;


import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.reactive.function.server.RouterFunction;
import org.springframework.web.reactive.function.server.RouterFunctions;
import org.springframework.web.reactive.function.server.ServerResponse;

import co.com.crediya.api.dto.ApplyLoanRqDTO;
import co.com.crediya.api.dto.ApplyLoanRsDTO;
import co.com.crediya.api.dto.PageResultLoanApplicationRsDTO;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.enums.ParameterIn;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;

import org.springdoc.core.annotations.RouterOperation;
import org.springdoc.core.annotations.RouterOperations;

@Configuration
public class RouterRest {

    @Bean
    @RouterOperations({
        @RouterOperation(
            path = "/api/v1/solicitud",
            method = RequestMethod.POST,
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
        ),
       @RouterOperation(
            path = "/api/v1/solicitud",
            method = RequestMethod.GET,
            produces = { "application/json" },
            beanClass = HandlerV1.class,
            beanMethod = "getLoanApplications",
            operation = @Operation(
                operationId = "getLoanApplications",
                summary = "List loan applications",
                description = "Retrieve a paginated list of loan applications with optional filters.",
                tags = { "Loan Application V1" },
                security = { @SecurityRequirement(name = "bearerAuth") },
                parameters = {
                    @Parameter(
                        name = "page",
                        description = "Page number (default 0)",
                        in = ParameterIn.QUERY,
                        schema = @Schema(type = "integer", example = "0")
                    ),
                    @Parameter(
                        name = "size",
                        description = "Page size (default 5)",
                        in = ParameterIn.QUERY,
                        schema = @Schema(type = "integer", example = "5")
                    ),
                    @Parameter(
                        name = "status",
                        description = "Filter by loan status (APPROVED, REJECTED, PENDING)",
                        in = ParameterIn.QUERY,
                        schema = @Schema(type = "string", example = "PENDING")
                    ),
                    @Parameter(
                        name = "loanType",
                        description = "Filter by loan type (PERSONAL, AUTO, STUDENT, BUSINESS, MICROCREDIT, HOUSING)",
                        in = ParameterIn.QUERY,
                        schema = @Schema(type = "string", example = "PERSONAL")
                    )
                },
                responses = {
                    @ApiResponse(
                        responseCode = "200",
                        description = "Successful page of loan applications",
                        content = @Content(
                            mediaType = "application/json",
                            schema = @Schema(
                                implementation = PageResultLoanApplicationRsDTO.class
                            )
                        )
                    ),
                    @ApiResponse(
                        responseCode = "400",
                        description = "Invalid filter or validation error",
                        content = @Content(
                            schema = @Schema(
                                example = "{\"code\":\"BUSINESS_ERROR\", \"message\":\"Invalid status type: X\"}"
                            )
                        )
                    ),
                    @ApiResponse(
                        responseCode = "401",
                        description = "Unauthorized, missing or invalid token",
                        content = @Content(
                            schema = @Schema(
                                example = "{\"code\":\"UNAUTHORIZED\", \"message\":\"Invalid or missing token\"}"
                            )
                        )
                    ),
                    @ApiResponse(
                        responseCode = "500",
                        description = "Internal server error",
                        content = @Content(
                            schema = @Schema(
                                example = "{\"code\":\"INTERNAL_ERROR\", \"message\":\"Unexpected error\"}"
                            )
                        )
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
