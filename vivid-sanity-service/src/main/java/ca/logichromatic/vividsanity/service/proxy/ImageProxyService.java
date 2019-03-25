package ca.logichromatic.vividsanity.service.proxy;


import ca.logichromatic.vividsanity.configuration.ApplicationProperties;
import ca.logichromatic.vividsanity.service.image.ImageOperationService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Slf4j
@Service
public class ImageProxyService {
    @Autowired
    private ApplicationProperties applicationProperties;

    @Autowired
    private ImageOperationService imageOperationService;

    public byte[] getImage(String imageId){
        return imageOperationService.getImageBytesFromS3(applicationProperties.getLocal().getBucket(), imageId);
    }
}
