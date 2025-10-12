package co.com.crediya.sqs.sender.common;

import co.com.crediya.sqs.sender.common.config.SqsProperties;
import co.com.crediya.sqs.sender.debtCapacityCalculation.properties.CapacitySqsProperties;
import co.com.crediya.sqs.sender.decision.properties.DecisionSqsProperties;
import lombok.RequiredArgsConstructor;
import lombok.extern.log4j.Log4j2;
import reactor.core.publisher.Mono;
import software.amazon.awssdk.services.sqs.SqsAsyncClient;
import software.amazon.awssdk.services.sqs.model.SendMessageRequest;
import software.amazon.awssdk.services.sqs.model.SendMessageResponse;

//@Service
@Log4j2
@RequiredArgsConstructor
public class SQSSender /*implements SomeGateway*/ {
    private final SqsProperties properties; 
    private final SqsAsyncClient client;

    public Mono<String> send(String message) {
        String queueUrl = (properties instanceof CapacitySqsProperties p) ? p.queueurl()
                          : ((DecisionSqsProperties) properties).queueurl();
        return Mono.fromCallable(() -> buildRequest(message, queueUrl))
                .flatMap(request -> Mono.fromFuture(client.sendMessage(request)))
                .doOnNext(response -> log.debug("Message sent {}", response.messageId()))
                .map(SendMessageResponse::messageId);
    }

    private SendMessageRequest buildRequest(String message, String queueUrl) {
        return SendMessageRequest.builder()
                .queueUrl(queueUrl)
                .messageBody(message)
                .build();
    }
}
