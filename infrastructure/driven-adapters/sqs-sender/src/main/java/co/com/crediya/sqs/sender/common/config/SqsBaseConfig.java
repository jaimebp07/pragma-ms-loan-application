package co.com.crediya.sqs.sender.common.config;

import java.net.URI;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import software.amazon.awssdk.auth.credentials.*;
import software.amazon.awssdk.metrics.MetricPublisher;
import software.amazon.awssdk.regions.Region;
import software.amazon.awssdk.services.sqs.SqsAsyncClient;
import software.amazon.awssdk.services.sqs.SqsAsyncClientBuilder;
import co.com.crediya.sqs.sender.common.SQSSender;

@Configuration
public class SqsBaseConfig {

    @Bean
    AwsCredentialsProviderChain awsCredentialsProviderChain() {
        return AwsCredentialsProviderChain.builder()
            .addCredentialsProvider(EnvironmentVariableCredentialsProvider.create())
            .addCredentialsProvider(SystemPropertyCredentialsProvider.create())
            .addCredentialsProvider(WebIdentityTokenFileCredentialsProvider.create())
            .addCredentialsProvider(ProfileCredentialsProvider.create())
            .addCredentialsProvider(ContainerCredentialsProvider.builder().build())
            .addCredentialsProvider(InstanceProfileCredentialsProvider.create())
            .build();
    }

    public SqsAsyncClient buildClient(SqsProperties props, MetricPublisher publisher, AwsCredentialsProvider creds) {
        SqsAsyncClientBuilder builder = SqsAsyncClient.builder()
            .region(Region.of(props.region()))
            .overrideConfiguration(o -> o.addMetricPublisher(publisher))
            .credentialsProvider(creds);

        if (props.endpoint() != null && !props.endpoint().isBlank()) {
            builder.endpointOverride(URI.create(props.endpoint()));
        }

        return builder.build();
    }

    public SQSSender buildSender(SqsProperties props, SqsAsyncClient client) {
        return new SQSSender(props, client);
    }
}
