package co.com.crediya.sqs.sender;

import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Component;

import co.com.crediya.model.pagedLoanApplication.gateways.LoanDecisionPublisherGateway;
import reactor.core.publisher.Mono;

@Component
public class LoanDecisionPublisherAdapter implements LoanDecisionPublisherGateway {

    private final SQSSender sqsSender;

    public LoanDecisionPublisherAdapter(@Qualifier("decisionSqsSender") SQSSender sqsSender) {
        this.sqsSender = sqsSender;
    }

    @Override
    public Mono<String> publish(String eventJson) {
        return sqsSender.send(eventJson);
    }
}
