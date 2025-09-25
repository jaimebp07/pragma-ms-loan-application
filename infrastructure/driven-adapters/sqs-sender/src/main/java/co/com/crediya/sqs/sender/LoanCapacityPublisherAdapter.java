package co.com.crediya.sqs.sender;

import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Component;

import co.com.crediya.model.loanapplication.LoanApplicationStatus;
import co.com.crediya.model.loanapplication.gateways.LoanCapacityPublisherGateway;
import lombok.RequiredArgsConstructor;
import reactor.core.publisher.Mono;

@Component
@RequiredArgsConstructor
public class LoanCapacityPublisherAdapter  implements LoanCapacityPublisherGateway {

    @Qualifier("capacitySqsSender")
    private final SQSSender sqsSender;

    @Override
    public Mono<LoanApplicationStatus> validateLoanPublish(String eventJson) {
        return sqsSender.send(eventJson)
            .map(responseStatus -> {
                return LoanApplicationStatus.fromValue(responseStatus);
            }
        );
    }
}
