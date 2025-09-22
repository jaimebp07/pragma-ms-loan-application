package co.com.crediya.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import co.com.crediya.model.customer.gateways.CustomerGateway;
import co.com.crediya.model.loanapplication.gateways.LoanAplicationRepository;
import co.com.crediya.model.pagedLoanApplication.gateways.EvaluationLoanApplicationGateway;
import co.com.crediya.model.security.TokenServiceGateway;
import co.com.crediya.usecase.applyloan.ApplyLoanUseCase;
import co.com.crediya.usecase.getloanapplications.GetLoanApplicationsUseCase;
import co.com.crediya.usecase.updateloanapplicationstatus.UpdateLoanApplicationStatusUseCase;

@Configuration
public class UseCasesConfig {
        
        @Bean
        ApplyLoanUseCase applyLoanUseCase(
                LoanAplicationRepository loanApplicationRepository, 
                CustomerGateway clientRepository, 
                TokenServiceGateway tokenServiceGateway) {
                return new ApplyLoanUseCase(loanApplicationRepository, clientRepository, tokenServiceGateway);
        }

        @Bean
        GetLoanApplicationsUseCase getLoanApplicationsUseCase(EvaluationLoanApplicationGateway repository, CustomerGateway customerGateway){
                return new GetLoanApplicationsUseCase(repository, customerGateway);
        }

        @Bean
        UpdateLoanApplicationStatusUseCase updateLoanApplicationStatusUseCase(LoanAplicationRepository loanAplicationRepository, CustomerGateway customerGateway){
                return new UpdateLoanApplicationStatusUseCase(loanAplicationRepository, customerGateway);
        }
}
