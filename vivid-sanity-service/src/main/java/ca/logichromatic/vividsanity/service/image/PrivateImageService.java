package ca.logichromatic.vividsanity.service.image;


import ca.logichromatic.vividsanity.configuration.ApplicationProperties;
import ca.logichromatic.vividsanity.model.ImageInfo;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Profile;
import org.springframework.stereotype.Service;

import java.io.InputStream;
import java.util.List;

@Slf4j
@Service
@Profile("private")
public class PrivateImageService implements ImageServiceInterface {
    @Autowired
    private ApplicationProperties applicationProperties;

    @Autowired
    private ImageOperationService imageOperationService;

    public List<ImageInfo> getImages() {
        log.error("I am the private bean!");
        return imageOperationService.getImages(applicationProperties.getPrivateBucket());
    }

    @Override
    public ImageInfo uploadImage(InputStream fileStream, int byteSize) {
        log.error("I am private upload bean");
       return imageOperationService.uploadImage(applicationProperties.getPrivateBucket(), fileStream, byteSize);
    }

}
