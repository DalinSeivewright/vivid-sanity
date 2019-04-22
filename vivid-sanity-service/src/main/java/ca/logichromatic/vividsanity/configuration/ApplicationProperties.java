package ca.logichromatic.vividsanity.configuration;

import ca.logichromatic.vividsanity.model.ServerMode;
import lombok.Data;
import lombok.Getter;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.stereotype.Component;

@Component
@Data
@ConfigurationProperties(prefix = "vivid")
public class ApplicationProperties {
    private static final String DEFAULT_CACHE_MAX_AGE = "1800";

    private ServerMode serverMode;
    private String cacheMaxAge;
    private SpecificProperties local;
    private SpecificProperties external;


    public String getCacheMaxAge() {
        if (this.cacheMaxAge == null) {
            return DEFAULT_CACHE_MAX_AGE;
        }
        return this.cacheMaxAge;
    }

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
