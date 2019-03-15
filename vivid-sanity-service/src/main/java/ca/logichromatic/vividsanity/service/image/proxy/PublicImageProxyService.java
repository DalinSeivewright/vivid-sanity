package ca.logichromatic.vividsanity.service.image.proxy;


import ca.logichromatic.vividsanity.configuration.ApplicationProperties;
import ca.logichromatic.vividsanity.service.image.ImageOperationService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.stereotype.Service;

@Slf4j
@Service
@ConditionalOnProperty(prefix="vivid", name="serverMode", havingValue = "public")
public class PublicImageProxyService implements ImageProxyServiceInterface {
    @Autowired
    private ApplicationProperties applicationProperties;

    @Autowired
    private ImageOperationService imageOperationService;

    public byte[] getImage(String imageId){
        return imageOperationService.getImage(applicationProperties.getPublicBucket(), imageId);
    }
}
