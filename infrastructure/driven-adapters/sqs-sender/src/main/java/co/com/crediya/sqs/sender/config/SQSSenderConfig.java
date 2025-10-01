package co.com.crediya.sqs.sender.config;

import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import co.com.crediya.sqs.sender.SQSSender;
import software.amazon.awssdk.auth.credentials.AwsCredentialsProviderChain;
import software.amazon.awssdk.auth.credentials.ContainerCredentialsProvider;
import software.amazon.awssdk.auth.credentials.EnvironmentVariableCredentialsProvider;
import software.amazon.awssdk.auth.credentials.InstanceProfileCredentialsProvider;
import software.amazon.awssdk.auth.credentials.ProfileCredentialsProvider;
import software.amazon.awssdk.auth.credentials.SystemPropertyCredentialsProvider;
import software.amazon.awssdk.auth.credentials.WebIdentityTokenFileCredentialsProvider;
import software.amazon.awssdk.metrics.MetricPublisher;
import software.amazon.awssdk.regions.Region;
import software.amazon.awssdk.services.sqs.SqsAsyncClient;

import java.net.URI;

@Configuration
@ConditionalOnMissingBean(SqsAsyncClient.class)
public class SQSSenderConfig {

/* hoy
    @Bean
    @ConfigurationProperties(prefix = "adapters.sqs.decisions")
    DecisionSqsProperties decisionSqsProperties() {
        //return new DecisionSqsProperties(null, null, null);
        return new DecisionSqsProperties();
    }*/

    @Bean("decisionSqsClient")
    //SqsAsyncClient configSqs(@Qualifier("decisionSqsProperties") DecisionSqsProperties properties, MetricPublisher publisher) {
    SqsAsyncClient configSqs( DecisionSqsProperties properties, MetricPublisher publisher) {
    
        return SqsAsyncClient.builder()
                .endpointOverride(resolveEndpoint(properties)) // Permite localStack
                .region(Region.of(properties.region()))
                .overrideConfiguration(o -> o.addMetricPublisher(publisher))
                .credentialsProvider(getProviderChain()) // Cadena de credenciales
                .build();
    }

    private AwsCredentialsProviderChain getProviderChain() {
        return AwsCredentialsProviderChain.builder()
                .addCredentialsProvider(EnvironmentVariableCredentialsProvider.create())
                .addCredentialsProvider(SystemPropertyCredentialsProvider.create())
                .addCredentialsProvider(WebIdentityTokenFileCredentialsProvider.create())
                .addCredentialsProvider(ProfileCredentialsProvider.create())
                .addCredentialsProvider(ContainerCredentialsProvider.builder().build())
                .addCredentialsProvider(InstanceProfileCredentialsProvider.create())
                .build();
    }

    private URI resolveEndpoint(DecisionSqsProperties properties) {
        if (properties.endpoint() != null) {
            return URI.create(properties.endpoint());
        }
        return null;
    }

    @Bean("decisionSqsSender")
    SQSSender decisionSqsSender(
            @Qualifier("decisionSqsClient") SqsAsyncClient client,
            DecisionSqsProperties props) {
        return new SQSSender(props, client);
    }
}
