package ca.logichromatic.vividsanity.service.image;

import ca.logichromatic.vividsanity.entity.ImageInfo;
import ca.logichromatic.vividsanity.model.VisibilityStatus;
import ca.logichromatic.vividsanity.repository.external.ExternalImageInfoRepository;
import ca.logichromatic.vividsanity.repository.local.LocalImageInfoRepository;
import ca.logichromatic.vividsanity.type.DatabaseTarget;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@Transactional
@Slf4j
public class ImageInfoDbService {
    @Autowired(required = false)
    private LocalImageInfoRepository localImageInfoRepository;

    @Autowired
    private ExternalImageInfoRepository externalImageInfoRepository;

    public ImageInfo save(ImageInfo imageInfo) {
        if (imageInfo.getVisibility() == VisibilityStatus.PUBLIC) {
            log.info("Saving to external database");
            externalImageInfoRepository.save(imageInfo);
        }
        return localImageInfoRepository.save(imageInfo);
    }
}
