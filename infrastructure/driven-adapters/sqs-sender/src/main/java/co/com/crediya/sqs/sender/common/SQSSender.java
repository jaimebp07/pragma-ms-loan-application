package co.com.crediya.sqs.sender.common;

import java.util.HashMap;
import java.util.Map;

import co.com.crediya.sqs.sender.common.config.SqsProperties;
import lombok.RequiredArgsConstructor;
import lombok.extern.log4j.Log4j2;
import reactor.core.publisher.Mono;
import software.amazon.awssdk.services.sqs.SqsAsyncClient;
import software.amazon.awssdk.services.sqs.model.MessageAttributeValue;
import software.amazon.awssdk.services.sqs.model.SendMessageRequest;
import software.amazon.awssdk.services.sqs.model.SendMessageResponse;

//@Service
@Log4j2
@RequiredArgsConstructor
public class SQSSender /*implements SomeGateway*/ {
    private final SqsProperties properties; 
    private final SqsAsyncClient client;

    public Mono<String> send(String message) {
        return send(message, Map.of());
    }

    /**
     * Envía un mensaje con atributos adicionales (útil para routing, auditoría o trazabilidad).
     */
    public Mono<String> send(String message, Map<String, String> attributes) {
        String queueUrl = properties.queueurl();

        // Construir los atributos del mensaje
        Map<String, MessageAttributeValue> messageAttributes = new HashMap<>();
        attributes.forEach((key, value) ->
                messageAttributes.put(key, MessageAttributeValue.builder()
                        .stringValue(value)
                        .dataType("String")
                        .build()));

        SendMessageRequest request = buildRequest(message, queueUrl, messageAttributes);
        
        return Mono.fromFuture(client.sendMessage(request))
                .doOnSubscribe(sub -> log.info("Enviando mensaje a cola [{}]: {}", queueUrl, message))
                .doOnNext(response -> log.info("Mensaje enviado con ID: {}", response.messageId()))
                .doOnError(e -> log.error("Error enviando mensaje a cola [{}]", queueUrl, e))
                .map(SendMessageResponse::messageId);
    }
    
    private SendMessageRequest buildRequest(String message, String queueUrl, Map<String, MessageAttributeValue> messageAttributes) {
        return SendMessageRequest.builder()
                .queueUrl(queueUrl)
                .messageBody(message)
                .build();
    }
}
