package ca.logichromatic.vividsanity.configuration;

import ca.logichromatic.vividsanity.model.ServerMode;
import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.stereotype.Component;

@Component
@Data
@ConfigurationProperties(prefix = "vivid")
public class ApplicationProperties {
    private ServerMode serverMode;
    private SpecificProperties local;
    private SpecificProperties external;


    @Data
    public static class SpecificProperties {
        private BucketProperties bucket;
    }

    @Data
    public static class BucketProperties {
        private String uri;
        private String region;
        private String bucketKey;
        private String accessKey;
        private String secretKey;
    }
}
