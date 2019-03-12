package ca.logichromatic.vividsanity.configuration;

import lombok.Data;
import lombok.Getter;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.stereotype.Component;

@Component
@Data
@ConfigurationProperties(prefix = "vivid")
public class ApplicationProperties {

    private MinioProperties minio = new MinioProperties();
    private ImageProxyProperties imageProxy = new ImageProxyProperties();

    @Data
    public static class MinioProperties {
        private String uri;
        private String accessKey;
        private String secretKey;
    }

    @Data
    public static class ImageProxyProperties {
        private String zuulRouteId;
    }
}
