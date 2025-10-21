package co.com.crediya.sqs.sender.decision.properties;

import org.springframework.boot.context.properties.ConfigurationProperties;

import co.com.crediya.sqs.sender.common.config.SqsProperties;

@ConfigurationProperties(prefix = "adapters.sqs.decisions")
public record DecisionSqsProperties(
     String region,
     String queueurl,
     String endpoint
) implements SqsProperties {}
