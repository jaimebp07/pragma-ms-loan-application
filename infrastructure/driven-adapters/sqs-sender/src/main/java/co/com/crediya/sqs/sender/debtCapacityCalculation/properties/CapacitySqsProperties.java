package co.com.crediya.sqs.sender.debtCapacityCalculation.properties;

import org.springframework.boot.context.properties.ConfigurationProperties;

import co.com.crediya.sqs.sender.common.config.SqsProperties;

@ConfigurationProperties(prefix = "adapters.sqs.capacity-calculation")
public record CapacitySqsProperties(
    String region, 
    String queueurl, 
    String endpoint
) implements SqsProperties {}
