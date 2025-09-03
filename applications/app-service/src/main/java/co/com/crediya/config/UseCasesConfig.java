package co.com.crediya.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import co.com.crediya.model.loanaplication.gateways.LoanAplicationRepository;
import co.com.crediya.usecase.applyloan.ApplyLoanUseCase;

@Configuration
public class UseCasesConfig {
        
        @Bean
        ApplyLoanUseCase applyLoanUseCase(LoanAplicationRepository loanApplicationRepository) {
                return new ApplyLoanUseCase(loanApplicationRepository);
        }
}
