package co.com.crediya.sqs.sender.debtCapacityResult;


import java.time.Duration;

import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.boot.context.event.ApplicationReadyEvent;
import org.springframework.context.ApplicationListener;
import org.springframework.stereotype.Service;

import co.com.crediya.sqs.sender.debtCapacityResult.properties.CapacityResultSqsProperties;
import lombok.extern.log4j.Log4j2;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;
import software.amazon.awssdk.services.sqs.SqsAsyncClient;
import software.amazon.awssdk.services.sqs.model.Message;

@Service
@Log4j2
public class LoanCapacityResultListener implements ApplicationListener<ApplicationReadyEvent> {

    private final SqsAsyncClient sqsClient;
    private final CapacityResultSqsProperties properties;

    public LoanCapacityResultListener(@Qualifier("capacityResultSqsClient") SqsAsyncClient sqsClient, CapacityResultSqsProperties properties){
        this.sqsClient = sqsClient;
        this.properties = properties;
    }
    
    /**
     * Se ejecuta automáticamente cuando la aplicación ha terminado de arrancar.
     * Aquí inicia el flujo reactivo que consulta la cola de SQS.
     */
    @Override
    public void onApplicationEvent(ApplicationReadyEvent event) {
        log.info("Iniciando listener de resultados de capacidad de crédito...");
        startListening();
    }

    private void startListening() {
        Flux.interval(Duration.ofSeconds(5))
            .flatMap(tick ->
                Mono.fromFuture(() ->
                    sqsClient.receiveMessage(r -> r
                        .queueUrl(properties.queueurl())
                        .waitTimeSeconds(10)
                        .maxNumberOfMessages(5))))
            .flatMapIterable(resp -> resp.messages())
            .flatMap(this::processMessage)
            .onErrorContinue((err, obj) ->
                log.error("Error al recibir mensajes de SQS", err))
            .subscribe();
    }

    private Mono<Void> processMessage(Message msg) {
        try {
            String json = msg.body();
            log.info("Resultado de capacidad recibido: {}", json);

            return Mono.fromFuture(() ->
                sqsClient.deleteMessage(r -> r
                    .queueUrl(properties.queueurl())
                    .receiptHandle(msg.receiptHandle())))
                .then();
        } catch (Exception e) {
            log.error("Error procesando mensaje de SQS", e);
            return Mono.empty();
        }
    }
}
