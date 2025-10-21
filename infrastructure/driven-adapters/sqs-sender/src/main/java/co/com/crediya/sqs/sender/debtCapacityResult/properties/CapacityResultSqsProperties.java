package co.com.crediya.sqs.sender.debtCapacityResult.properties;

import org.springframework.boot.context.properties.ConfigurationProperties;

import co.com.crediya.sqs.sender.common.config.SqsProperties;

@ConfigurationProperties(prefix = "adapters.sqs.capacity-result")
public record CapacityResultSqsProperties (
    String region, 
    String queueurl, 
    String endpoint
) implements SqsProperties {}
