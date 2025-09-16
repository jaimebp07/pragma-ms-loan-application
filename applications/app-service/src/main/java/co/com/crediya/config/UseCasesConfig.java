package co.com.crediya.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import co.com.crediya.model.customer.gateways.CustomerGateway;
import co.com.crediya.model.loanapplication.gateways.LoanAplicationRepository;
import co.com.crediya.model.security.TokenServiceGateway;
import co.com.crediya.usecase.applyloan.ApplyLoanUseCase;
import co.com.crediya.usecase.getloanapplications.GetLoanApplicationsUseCase;

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
        GetLoanApplicationsUseCase getLoanApplicationsUseCase(LoanAplicationRepository loanApplicationRepository){
                return new GetLoanApplicationsUseCase(loanApplicationRepository);
        }
}
