package co.com.crediya.sqs.sender.debtCapacityCalculation;

import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Component;

import co.com.crediya.model.loanapplication.LoanApplicationStatus;
import co.com.crediya.model.loanapplication.gateways.LoanCapacityPublisherGateway;
import co.com.crediya.sqs.sender.common.SQSSender;
import lombok.extern.log4j.Log4j2;
import reactor.core.publisher.Mono;

@Component
@Log4j2
public class CapacityCalculationPublisherAdapter  implements LoanCapacityPublisherGateway {

    private final SQSSender sqsSender;

     public CapacityCalculationPublisherAdapter(@Qualifier("capacitySqsSender") SQSSender sqsSender) {
        this.sqsSender = sqsSender;
    }

    
    @Override
    public Mono<LoanApplicationStatus> validateLoanPublish(String eventJson) {
        log.info("Enviando mensaje a SQS para validación de capacidad: {}", eventJson);
        return sqsSender.send(eventJson)
                .doOnSubscribe(sub ->
                        log.debug("Starting SQS send operation"))
                .doOnSuccess(messageId ->
                        log.info("Message successfully sent to SQS. messageId={}", messageId))
                .doOnError(error ->
                        log.error("Failed to send message to SQS: {}", error.getMessage(), error))
                .map(responseStatus -> {
                    log.debug("Mapping SQS response to LoanApplicationStatus. Raw response: {}", responseStatus);
                    return LoanApplicationStatus.fromValue(responseStatus);
                });
    }
}
