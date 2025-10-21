package co.com.crediya.sqs.sender.common;

import java.time.Duration;
import java.util.function.Function;

import org.springframework.stereotype.Component;

import lombok.extern.log4j.Log4j2;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;
import software.amazon.awssdk.services.sqs.SqsAsyncClient;
import software.amazon.awssdk.services.sqs.model.Message;

@Component
@Log4j2
public class SQSListener {

    public void startListening(SqsAsyncClient sqsClient, String queueUrl, Function<Message, Mono<Void>> handler) {
        log.info("Iniciando listener genérico para la cola: {}", queueUrl);

        Flux.interval(Duration.ofSeconds(5))
            .flatMap(tick -> Mono.fromFuture(() ->
                sqsClient.receiveMessage(r -> r
                    .queueUrl(queueUrl)
                    .waitTimeSeconds(10)
                    .maxNumberOfMessages(5))))
            .flatMapIterable(resp -> resp.messages())
            .flatMap(handler)
            .onErrorContinue((err, obj) ->
                log.error("Error al recibir mensajes de SQS [{}]", queueUrl, err))
            .subscribe();
    }

    public Mono<Void> deleteMessage(SqsAsyncClient sqsClient, String queueUrl, Message msg) {
        return Mono.fromFuture(() ->
                sqsClient.deleteMessage(r -> r
                    .queueUrl(queueUrl)
                    .receiptHandle(msg.receiptHandle())))
            .then();
    }
}
