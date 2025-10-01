package co.com.crediya.usecase.updateloanapplicationstatus;

import java.time.Instant;
import java.util.HashSet;
import java.util.Optional;
import java.util.Set;
import java.util.UUID;

import co.com.crediya.model.customer.gateways.CustomerGateway;
import co.com.crediya.model.loanapplication.LoanApplicationStatus;
import co.com.crediya.model.loanapplication.gateways.LoanAplicationRepository;
import co.com.crediya.model.pagedLoanApplication.EvaluationLoanApplication;
import co.com.crediya.model.pagedLoanApplication.LoanDecisionEvent;
import co.com.crediya.model.pagedLoanApplication.gateways.LoanDecisionPublisherGateway;
import reactor.core.publisher.Mono;

public class UpdateLoanApplicationStatusUseCase {
    private LoanAplicationRepository loanAplicationRepository;
    private final CustomerGateway customerGateway;
    private final LoanDecisionPublisherGateway loanDecisionPublisher;

    public UpdateLoanApplicationStatusUseCase(LoanAplicationRepository loanAplicationRepository, CustomerGateway customerGateway, LoanDecisionPublisherGateway loanDecisionPublisher){
        this.loanAplicationRepository = loanAplicationRepository;
        this.customerGateway = customerGateway;
        this.loanDecisionPublisher = loanDecisionPublisher;
    }

    public Mono<EvaluationLoanApplication> updateLoanApplicationStatus(UUID loanApplicationID, LoanApplicationStatus status, Optional<String> comment) {
        return loanAplicationRepository.updateLoanAplicationStatus(loanApplicationID, status, comment)
        .flatMap( loan -> {
                Set<UUID> customerId = new HashSet<>(Set.of(loan.getClientId()));
                customerGateway.findByIdList(customerId);

                EvaluationLoanApplication result = new EvaluationLoanApplication.Builder()
                    .loanApplication(loan)
                    .email("andresodigo07@gmail.com") // TODO reemplazar por la informacion real del cliente
                    .name("Pepito")
                    .build();
                LoanDecisionEvent event = new LoanDecisionEvent(
                        loan.getId(),
                        result.getEmail(),
                        status.name(),
                        result.getName(),
                        Instant.now()
                );

                return Mono.fromCallable(event::toJson)
                           .flatMap(loanDecisionPublisher::publish)
                           .doOnSuccess(id -> System.out.println("Mensaje SQS enviado, id: " + id))
                           .thenReturn(result);
            }
        );
    }
}
