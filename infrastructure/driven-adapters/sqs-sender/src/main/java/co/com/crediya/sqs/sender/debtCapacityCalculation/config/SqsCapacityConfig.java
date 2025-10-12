package co.com.crediya.sqs.sender.debtCapacityCalculation.config;

import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import co.com.crediya.sqs.sender.common.SQSSender;
import co.com.crediya.sqs.sender.common.config.SqsBaseConfig;
import co.com.crediya.sqs.sender.debtCapacityCalculation.properties.CapacitySqsProperties;
import software.amazon.awssdk.auth.credentials.AwsCredentialsProviderChain;
import software.amazon.awssdk.metrics.MetricPublisher;
import software.amazon.awssdk.services.sqs.SqsAsyncClient;

@Configuration
@EnableConfigurationProperties(CapacitySqsProperties.class)
public class SqsCapacityConfig {
  
    private final SqsBaseConfig baseConfig = new SqsBaseConfig();
    
    @Bean("capacitySqsClient")
    SqsAsyncClient capacityClient(
            CapacitySqsProperties props,
            MetricPublisher publisher,
            AwsCredentialsProviderChain creds) {
        return baseConfig.buildClient(props, publisher, creds);
    }

    @Bean("capacitySqsSender")
   SQSSender capacitySender(
            @Qualifier("capacitySqsClient") SqsAsyncClient client,
            CapacitySqsProperties props) {
        return baseConfig.buildSender(props, client);
    }
}
