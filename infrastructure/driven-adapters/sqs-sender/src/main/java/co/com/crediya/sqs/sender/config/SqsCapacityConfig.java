package co.com.crediya.sqs.sender.config;

import java.net.URI;

import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import co.com.crediya.sqs.sender.SQSSender;
import software.amazon.awssdk.auth.credentials.AwsCredentialsProviderChain;
import software.amazon.awssdk.auth.credentials.EnvironmentVariableCredentialsProvider;
import software.amazon.awssdk.metrics.MetricPublisher;
import software.amazon.awssdk.regions.Region;
import software.amazon.awssdk.services.sqs.SqsAsyncClient;

@Configuration
@EnableConfigurationProperties(CapacitySqsProperties.class)
public class SqsCapacityConfig {
    /*@Bean
    @ConfigurationProperties(prefix = "adapters.sqs.capacity")
    CapacitySqsProperties capacitySqsProperties() {
        return new CapacitySqsProperties();
    }*/

    @Bean("capacitySqsClient")
    SqsAsyncClient capacitySqsClient(CapacitySqsProperties capacitySqsProperties,
                     MetricPublisher publisher) {
    String endpoint = capacitySqsProperties.endpoint();
    System.out.println(" ---------------------------------> endpoint: "+ endpoint);
    String endpointToUse = (endpoint == null || endpoint.isBlank())
    
        ? "https://sqs.us-east-2.amazonaws.com/484558640369/loan-capacity-check"
        : endpoint;
    return SqsAsyncClient.builder()
        .endpointOverride(URI.create(endpointToUse))
        .region(Region.of(capacitySqsProperties.region()))
        .overrideConfiguration(o -> o.addMetricPublisher(publisher))
        .credentialsProvider(AwsCredentialsProviderChain.builder()
            .addCredentialsProvider(EnvironmentVariableCredentialsProvider.create())
            .build())
        .build();
    }

    @Bean("capacitySqsSender")
    public SQSSender capacitySqsSender(
            @Qualifier("capacitySqsClient") SqsAsyncClient client,
            CapacitySqsProperties props) {
        return new SQSSender(props, client);
    }
}
