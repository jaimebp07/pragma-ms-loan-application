package co.com.crediya.usecase.updateloanapplicationstatus;

import java.util.HashSet;
import java.util.Optional;
import java.util.Set;
import java.util.UUID;

import co.com.crediya.model.customer.gateways.CustomerGateway;
import co.com.crediya.model.loanapplication.LoanApplicationStatus;
import co.com.crediya.model.loanapplication.gateways.LoanAplicationRepository;
import co.com.crediya.model.pagedLoanApplication.EvaluationLoanApplication;
import reactor.core.publisher.Mono;

public class UpdateLoanApplicationStatusUseCase {

    private LoanAplicationRepository loanAplicationRepository;
    private final CustomerGateway customerGateway;

    public UpdateLoanApplicationStatusUseCase(LoanAplicationRepository loanAplicationRepository, CustomerGateway customerGateway){
        this.loanAplicationRepository = loanAplicationRepository;
        this.customerGateway = customerGateway;
    }

    public Mono<EvaluationLoanApplication> updateLoanAplicationStatus(UUID loanApplicationID, LoanApplicationStatus status, Optional<String> comment) {
        return loanAplicationRepository.updateLoanAplicationStatus(loanApplicationID, status, comment).flatMap( loan -> {

                
                Set<UUID> customerId = new HashSet<>(Set.of(loan.getClientId()));
                customerGateway.findByIdList(customerId);

                EvaluationLoanApplication result = new EvaluationLoanApplication.Builder()
                    .loanApplication(loan)
                    .email("andresodigo07@gmail.com") // TODO reemplazar por la informacion real del cliente
                    .name("Pepito")
                    .build();
                return Mono.just(result);
            }
        );
    }
}
