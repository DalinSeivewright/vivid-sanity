package ca.logichromatic.vividsanity.service.image.proxy;


import ca.logichromatic.vividsanity.configuration.ApplicationProperties;
import ca.logichromatic.vividsanity.service.image.ImageOperationService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Profile;
import org.springframework.stereotype.Service;

@Slf4j
@Service
@Profile("private")
public class PrivateImageProxyService implements ImageProxyServiceInterface {
    @Autowired
    private ApplicationProperties applicationProperties;

    @Autowired
    private ImageOperationService imageOperationService;

    public byte[] getImage(String imageId){
        return imageOperationService.getImage(applicationProperties.getPrivateBucket(), imageId);
    }
}