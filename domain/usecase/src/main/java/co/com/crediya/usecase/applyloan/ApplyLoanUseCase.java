package co.com.crediya.usecase.applyloan;

import java.math.BigDecimal;
import java.util.HashSet;
import java.util.List;
import java.util.Optional;
import java.util.Set;
import java.util.UUID;

import co.com.crediya.model.customer.Customer;
import co.com.crediya.model.customer.gateways.CustomerGateway;
import co.com.crediya.model.exceptions.BusinessException;
import co.com.crediya.model.exceptions.ErrorCode;
import co.com.crediya.model.loanapplication.LoanApplication;
import co.com.crediya.model.loanapplication.LoanApplicationStatus;
import co.com.crediya.model.loanapplication.filter.LoanAplicationFilter;
import co.com.crediya.model.loanapplication.gateways.LoanAplicationRepository;
import co.com.crediya.model.loanapplication.gateways.LoanCapacityPublisherGateway;
import co.com.crediya.model.loanapplication.validator.LoanAplicationValidator;
import co.com.crediya.model.pagedLoanApplication.EvaluationLoanApplication;
import co.com.crediya.model.pagedLoanApplication.gateways.EvaluationLoanApplicationGateway;
import co.com.crediya.model.security.TokenServiceGateway;
import reactor.core.publisher.Mono;

import co.com.crediya.usecase.utils.Utils;

public class ApplyLoanUseCase {

    private final LoanAplicationRepository loanAplicationRepository;
    private final CustomerGateway clientRepository;
    private final TokenServiceGateway tokenServiceGateway;
    private final LoanCapacityPublisherGateway loanCapacityPublisherGateway;
    private final EvaluationLoanApplicationGateway evaluationLoanApplicationGateway;
    
    public ApplyLoanUseCase( LoanAplicationRepository loanAplicationRepository, 
            CustomerGateway clientRepository, 
            TokenServiceGateway tokenServiceGateway, 
            LoanCapacityPublisherGateway loanCapacityPublisherGateway,
            EvaluationLoanApplicationGateway evaluationLoanApplicationGateway ) {
        this.loanAplicationRepository = loanAplicationRepository;
        this.clientRepository = clientRepository;
        this.tokenServiceGateway = tokenServiceGateway;
        this.loanCapacityPublisherGateway = loanCapacityPublisherGateway;
        this.evaluationLoanApplicationGateway = evaluationLoanApplicationGateway;
    }

    public Mono<LoanApplication> applyLoan(LoanApplication loanAplication) {

        return tokenServiceGateway.getAuthUserId().flatMap(clientId -> {
                        if(!loanAplication.getClientId().equals(clientId)){
                            return Mono.error(new BusinessException(ErrorCode.UNAUTHORIZED, 
                                "The request client does not match the token"));
                        }
                        return Mono.just(loanAplication);
                    }
                )
                .doOnNext(LoanAplicationValidator::validate)
                .flatMap(app -> clientRepository.existsById(app.getClientId()) 
                        .flatMap(exists -> {

                            if (!exists) {
                                return Mono.error(new BusinessException(ErrorCode.CLIENT_NOT_FOUND, "Client not found"));
                            }
                            return Mono.just(app);
                        })
                )
                .flatMap(loan -> 
                    loanAplicationRepository.isAutomaticValidation(loan.getLoanType())
                        .flatMap(isAutomaticValidation -> {
                                if(isAutomaticValidation){
                                    return automaticallyValidate(loan);
                                } else {
                                    return Mono.just(withPendingStatus(loan));
                                }
                            }
                        )
                )
                .flatMap(loanAplicationRepository::applyLoan)
                .onErrorMap(ex -> {
                    if (ex instanceof BusinessException) {
                        return ex;
                    }
                    if (ex.getMessage() != null && ex.getMessage().contains("R2DBC")) {
                        return new BusinessException(ErrorCode.DB_ERROR, "Database connection failed");
                    }
                    return new BusinessException(ErrorCode.UNEXPECTED_ERROR, "Unexpected error: " + ex.getMessage());
                });
    }

    private Mono<LoanApplication> automaticallyValidate(LoanApplication loan) {
        UUID customerUuid = loan.getClientId();
        LoanAplicationFilter filter = new LoanAplicationFilter(Optional.of(LoanApplicationStatus.APPROVED),Optional.empty(), Optional.of(customerUuid));
        Mono<List<EvaluationLoanApplication>> evalLoansList = evaluationLoanApplicationGateway.findPaged(0, 100, filter).collectList();
        
        return evalLoansList.flatMap(evalLoans -> {

                    BigDecimal currentMonthlyDebt = calculateCurrentMonthlyDebt(evalLoans);
                    Set<UUID> uuidCustomer = new HashSet<UUID>(Set.of(customerUuid));
                    
                    return clientRepository.findByIdList(uuidCustomer)
                        .flatMap( customers -> {
                                Customer firstCustomer = customers.iterator().next();
                                String sqsCapacityMessage = toJson(firstCustomer, loan, currentMonthlyDebt);
                                return loanCapacityPublisherGateway.validateLoanPublish(sqsCapacityMessage).flatMap(loanStatusUpdated -> {
                                    LoanApplication loanUpdated = loan.toBuilder().status(loanStatusUpdated).build();
                                    return Mono.just(loanUpdated);
                                });
                            }
                        );
                }
            );
    }

    private BigDecimal calculateCurrentMonthlyDebt(List<EvaluationLoanApplication> loansApprovedByCustomer) {
        return loansApprovedByCustomer.stream()
                .map(eval -> Utils.calculateMonthlyAmount(eval.getLoanApplication(), eval.getInterestRate()))
                .reduce(BigDecimal.ZERO, BigDecimal::add);
    }

    private String toJson(Customer customer, LoanApplication loan, BigDecimal currentMonthlyDebt) {
        String message =
                "{"
                + "\"loan\":{"
                    + "\"id\":\"" + loan.getId() + "\","
                    + "\"clientId\":\"" + loan.getClientId() + "\","
                    + "\"amount\":" + loan.getAmount() + ","
                    + "\"term\":" + loan.getTerm() + ","
                    + "\"loanType\":\"" + loan.getLoanType() + "\","
                    + "\"status\":\"" + loan.getStatus() + "\""
                + "},"
                + "\"customer\":{"
                    + "\"id\":\"" + customer.id() + "\","
                    + "\"firstName\":\"" + escape(customer.firstName()) + "\","
                    + "\"lastName\":\"" + escape(customer.lastName()) + "\","
                    + "\"email\":\"" + escape(customer.email()) + "\","
                    + "\"baseSalary\":" + customer.baseSalary()
                + "},"
                + "\"currentMonthlyDebt\":" + currentMonthlyDebt
                + "}";
        return message;
    }

    private static String escape(String value) {
        if (value == null) return "";
        return value.replace("\"", "\\\"");
    }

    private LoanApplication withPendingStatus(LoanApplication loanAplication) {
        return loanAplication.toBuilder()
                .status(LoanApplicationStatus.PENDING)
                .build();
    }
}
