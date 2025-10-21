package co.com.crediya.sqs.sender.debtCapacityResult;

import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.boot.context.event.ApplicationReadyEvent;
import org.springframework.context.ApplicationListener;
import org.springframework.stereotype.Service;

import co.com.crediya.sqs.sender.common.SQSListener;
import co.com.crediya.sqs.sender.debtCapacityResult.properties.CapacityResultSqsProperties;
import lombok.extern.log4j.Log4j2;
import reactor.core.publisher.Mono;
import software.amazon.awssdk.services.sqs.SqsAsyncClient;
import software.amazon.awssdk.services.sqs.model.Message;

@Service
@Log4j2
public class LoanCapacityResultListener implements ApplicationListener<ApplicationReadyEvent> {

    private final SqsAsyncClient sqsClient;
    private final CapacityResultSqsProperties properties;
    private final SQSListener sqsListener;

    public LoanCapacityResultListener(
        SQSListener sqsListener,
        @Qualifier("capacityResultSqsClient") SqsAsyncClient sqsClient, 
        CapacityResultSqsProperties properties){
        this.sqsListener = sqsListener;
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
        sqsListener.startListening(sqsClient, properties.queueurl(), this::processMessage);
    }

    private Mono<Void> processMessage(Message msg) {
         try {
            String json = msg.body();
            log.info("Resultado de capacidad recibido: {}", json);

            return sqsListener.deleteMessage(sqsClient, properties.queueurl(), msg);
        } catch (Exception e) {
            log.error("Error procesando mensaje de SQS", e);
            return Mono.empty();
        }
    }
}
