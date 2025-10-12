package co.com.crediya.sqs.sender.debtCapacityResult.config;

import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import co.com.crediya.sqs.sender.common.SQSSender;
import co.com.crediya.sqs.sender.common.config.SqsBaseConfig;
import co.com.crediya.sqs.sender.debtCapacityResult.properties.CapacityResultSqsProperties;
import software.amazon.awssdk.auth.credentials.AwsCredentialsProviderChain;
import software.amazon.awssdk.metrics.MetricPublisher;
import software.amazon.awssdk.services.sqs.SqsAsyncClient;

@Configuration
@EnableConfigurationProperties(CapacityResultSqsProperties.class)
public class SqsCapacityResultConfig {

    private final SqsBaseConfig baseConfig = new SqsBaseConfig();

    @Bean("capacityResultSqsClient")
    SqsAsyncClient capacityResultClient(
            CapacityResultSqsProperties props,
            MetricPublisher publisher,
            AwsCredentialsProviderChain creds) {
        return baseConfig.buildClient(props, publisher, creds);
    }

    @Bean("capacityResultSqsSender")
   SQSSender capacitySender(
            @Qualifier("capacitySqsClient") SqsAsyncClient client,
            CapacityResultSqsProperties props) {
        return baseConfig.buildSender(props, client);
    }
}
