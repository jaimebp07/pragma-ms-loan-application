package co.com.crediya.sqs.sender.config;

import java.net.URI;

import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import co.com.crediya.sqs.sender.SQSSender;
import software.amazon.awssdk.auth.credentials.AwsCredentialsProviderChain;
import software.amazon.awssdk.auth.credentials.EnvironmentVariableCredentialsProvider;
import software.amazon.awssdk.metrics.MetricPublisher;
import software.amazon.awssdk.regions.Region;
import software.amazon.awssdk.services.sqs.SqsAsyncClient;

@Configuration
public class SqsCapacityConfig {
    @Bean
    @ConfigurationProperties(prefix = "adapters.sqs.capacity")
    SQSSenderProperties capacitySqsProperties() {
        return new SQSSenderProperties(null,null,null);
    }

    @Bean("capacitySqsClient")
    SqsAsyncClient capacitySqsClient(SQSSenderProperties capacitySqsProperties,
                                            MetricPublisher publisher) {
        return SqsAsyncClient.builder()
                .endpointOverride(URI.create(capacitySqsProperties.endpoint() == null ? 
                                             "https://sqs.us-east-2.amazonaws.com" :
                                              capacitySqsProperties.endpoint()))
                .region(Region.of(capacitySqsProperties.region()))
                .overrideConfiguration(o -> o.addMetricPublisher(publisher))
                .credentialsProvider(AwsCredentialsProviderChain.builder()
                        .addCredentialsProvider(EnvironmentVariableCredentialsProvider.create())
                        .build())
                .build();
    }

    @Bean
    public SQSSender capacitySqsSender(
            @Qualifier("capacitySqsClient") SqsAsyncClient client,
            @Qualifier("capacitySqsProperties") SQSSenderProperties props) {
        return new SQSSender(props, client);
    }
}
