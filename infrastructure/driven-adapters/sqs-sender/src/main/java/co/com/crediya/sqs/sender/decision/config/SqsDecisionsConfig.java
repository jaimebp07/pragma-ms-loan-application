package co.com.crediya.sqs.sender.decision.config;

import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import co.com.crediya.sqs.sender.common.SQSSender;
import co.com.crediya.sqs.sender.common.config.SqsBaseConfig;
import co.com.crediya.sqs.sender.decision.properties.DecisionSqsProperties;
import software.amazon.awssdk.auth.credentials.AwsCredentialsProviderChain;
import software.amazon.awssdk.metrics.MetricPublisher;
import software.amazon.awssdk.services.sqs.SqsAsyncClient;

@Configuration
@EnableConfigurationProperties(DecisionSqsProperties.class)
public class SqsDecisionsConfig {

    private final SqsBaseConfig baseConfig = new SqsBaseConfig();

    @Bean("decisionSqsClient")
    SqsAsyncClient decisionClient(
            DecisionSqsProperties props,
            MetricPublisher publisher,
            AwsCredentialsProviderChain creds) {
        return baseConfig.buildClient(props, publisher, creds);
    }

    @Bean("decisionSqsSender")
    SQSSender decisionSender(
            @Qualifier("decisionSqsClient") SqsAsyncClient client,
            DecisionSqsProperties props) {
        return baseConfig.buildSender(props, client);
    }
}
