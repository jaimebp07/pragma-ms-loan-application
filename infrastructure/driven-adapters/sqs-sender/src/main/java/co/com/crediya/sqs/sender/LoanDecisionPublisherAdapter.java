package co.com.crediya.sqs.sender;

import org.springframework.stereotype.Component;

import co.com.crediya.model.pagedLoanApplication.gateways.LoanDecisionPublisherGateway;
import lombok.RequiredArgsConstructor;
import reactor.core.publisher.Mono;

@Component
@RequiredArgsConstructor
public class LoanDecisionPublisherAdapter implements LoanDecisionPublisherGateway {

    private final SQSSender sqsSender;

    @Override
    public Mono<String> publish(String eventJson) {
        return sqsSender.send(eventJson);
    }
}
