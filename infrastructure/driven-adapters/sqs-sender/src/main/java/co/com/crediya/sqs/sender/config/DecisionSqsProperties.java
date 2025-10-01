package co.com.crediya.sqs.sender.config;

import org.springframework.boot.context.properties.ConfigurationProperties;

@ConfigurationProperties(prefix = "adapters.sqs.decisions")
public record DecisionSqsProperties(
     String region,
     String queueurl,
     String endpoint
) implements SqsProperties {}
