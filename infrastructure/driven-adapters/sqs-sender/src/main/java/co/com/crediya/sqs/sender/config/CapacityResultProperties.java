package co.com.crediya.sqs.sender.config;

import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.stereotype.Component;

@Component
@ConfigurationProperties(prefix = "adapter.sqs.capacity-result")
public class CapacityResultProperties {

    private String resultQueueUrl;

    private String region;

    public String getResultQueueUrl() {
        return resultQueueUrl;
    }

    public void setResultQueueUrl(String resultQueueUrl) {
        System.out.println(" setResultQueueUrl, resultQueueUrl ----------> "+ resultQueueUrl);
        this.resultQueueUrl = resultQueueUrl;
    }

    public String getRegion() {
        return region;
    }

    public void setRegion(String region) {
        this.region = region;
    }
}
