package ca.logichromatic.vividsanity.service.image;

import ca.logichromatic.vividsanity.entity.ImageInfo;
import ca.logichromatic.vividsanity.repository.external.ExternalImageInfoRepository;
import ca.logichromatic.vividsanity.repository.local.LocalImageInfoRepository;
import ca.logichromatic.vividsanity.type.SpecialDatabaseAction;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Slf4j
@Service
@Transactional
public class ImagePersistenceService {
    @Autowired
    private LocalImageInfoRepository localImageInfoRepository;

    @Autowired(required = false)
    private ExternalImageInfoRepository externalImageInfoRepository;

    public ImageInfo save(ImageInfo imageInfo, SpecialDatabaseAction databaseAction) {
        ImageInfo updatedImageInfo = localImageInfoRepository.save(imageInfo);
        if (databaseAction == SpecialDatabaseAction.ADD_TO_EXTERNAL) {
            log.info("Saving to external db");
            externalImageInfoRepository.save(imageInfo);
        } else if (databaseAction == SpecialDatabaseAction.REMOVE_FROM_EXTERNAL) {
            log.info("Deleting from external db");
            externalImageInfoRepository.deleteById(imageInfo.getIdentifier());
        }
        return updatedImageInfo;
    }

    public ImageInfo find(String imageKey) {
        return localImageInfoRepository.findByImageKey(imageKey);
    }

}
