package co.com.crediya.sqs.sender;


import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;
import reactor.core.publisher.Mono;

@Component
@RequiredArgsConstructor
public class LoanDecisionPublisherAdapter implements LoanDecisionPublisher {

    private final SQSSender sqsSender;

    @Override
    public Mono<String> publish(String eventJson) {
        return sqsSender.send(eventJson);
    }
}