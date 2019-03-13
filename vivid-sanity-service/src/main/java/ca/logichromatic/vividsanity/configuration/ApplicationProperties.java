package ca.logichromatic.vividsanity.configuration;

import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.stereotype.Component;

@Component
@Data
@ConfigurationProperties(prefix = "vivid")
public class ApplicationProperties {

    private BucketProperties publicBucket = new BucketProperties();
    private BucketProperties privateBucket = new BucketProperties();

    @Data
    public static class BucketProperties {
        private String uri;
        private String bucketKey;
        private String accessKey;
        private String secretKey;
    }
}
