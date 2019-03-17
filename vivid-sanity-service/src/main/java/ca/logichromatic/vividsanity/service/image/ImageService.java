package ca.logichromatic.vividsanity.service.image;


import ca.logichromatic.vividsanity.configuration.ApplicationProperties;
import ca.logichromatic.vividsanity.entity.ImageInfo;
import ca.logichromatic.vividsanity.exception.ImageNotFoundException;
import ca.logichromatic.vividsanity.model.ImageInfoDto;
import ca.logichromatic.vividsanity.model.ImageInfoUpdate;
import ca.logichromatic.vividsanity.model.VisibilityStatus;
import ca.logichromatic.vividsanity.repository.external.ExternalImageInfoRepository;
import ca.logichromatic.vividsanity.repository.local.LocalImageInfoRepository;
import ca.logichromatic.vividsanity.transformer.ImageInfoTransformer;
import ca.logichromatic.vividsanity.transformer.ImageTagTransformer;
import ca.logichromatic.vividsanity.type.SpecialDatabaseAction;
import ca.logichromatic.vividsanity.util.ObjectIdGenerator;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;
import software.amazon.awssdk.services.s3.S3Client;

import java.io.IOException;
import java.util.List;
import java.util.stream.Collectors;

@Slf4j
@Service
@Transactional
public class ImageService {
    @Autowired
    private ApplicationProperties applicationProperties;

    @Autowired
    private ImageOperationService imageOperationService;

    @Autowired
    private ImagePersistenceService imagePersistenceService;

    @Autowired
    private LocalImageInfoRepository localImageInfoRepository;

    @Autowired(required = false)
    private ExternalImageInfoRepository externalImageInfoRepository;

    @Autowired
    private ImageInfoTransformer imageInfoTransformer;

    @Autowired
    private ImageTagTransformer imageTagTransformer;

    public List<ImageInfoDto> getImages() {
        log.error("I am the private bean!");
        return imageOperationService.getImages();
    }

    public ImageInfoDto uploadImage(MultipartFile multipartFile, int byteSize) throws IOException {
        log.error("I am private upload bean");
        String imageKey = generateUniqueId();
        ImageInfo imageInfo = ImageInfo.newInstance(imageKey).setVisibility(VisibilityStatus.PRIVATE);
        imagePersistenceService.save(imageInfo, SpecialDatabaseAction.NONE);

        S3Client localS3Client = imageOperationService.buildClient(applicationProperties.getLocal().getBucket());
        imageOperationService.uploadImage(localS3Client, applicationProperties.getLocal().getBucket().getBucketKey(), imageKey, multipartFile.getInputStream(), byteSize);
        if (imageInfo.getVisibility() == VisibilityStatus.PUBLIC) {
            log.info("Visiblity public!");
            S3Client externalS3Client = imageOperationService.buildClient(applicationProperties.getExternal().getBucket());
            imageOperationService.uploadImage(externalS3Client, applicationProperties.getExternal().getBucket().getBucketKey(), imageKey, multipartFile.getInputStream(), byteSize);
        }
        return imageInfoTransformer.toDto(imageInfo);
    }

    public ImageInfoDto updateImage(String imageKey, ImageInfoUpdate imageInfoUpdate) {
        ImageInfo imageInfo = imagePersistenceService.find(imageKey);
        if (imageInfo == null) {
            throw new ImageNotFoundException();
        }
        SpecialDatabaseAction databaseAction = SpecialDatabaseAction.NONE;
        if (imageInfo.getVisibility() != imageInfoUpdate.getVisibility()) {
            if (imageInfo.getVisibility() == VisibilityStatus.PRIVATE && imageInfoUpdate.getVisibility() == VisibilityStatus.PUBLIC) {
                databaseAction = SpecialDatabaseAction.ADD_TO_EXTERNAL;
            } else {
                databaseAction = SpecialDatabaseAction.REMOVE_FROM_EXTERNAL;
            }
        }

        imageInfo.setDescription(imageInfoUpdate.getDescription())
                .setTags(imageInfoUpdate.getTags().stream().map(tag -> imageTagTransformer.toEntity(imageInfo.getIdentifier(), tag)).collect(Collectors.toList()))
                .setVisibility(imageInfoUpdate.getVisibility());
        imagePersistenceService.save(imageInfo, databaseAction);

        if (databaseAction != SpecialDatabaseAction.NONE) {
            S3Client externalS3Client = imageOperationService.buildClient(applicationProperties.getExternal().getBucket());
            if (databaseAction == SpecialDatabaseAction.ADD_TO_EXTERNAL) {
                log.info("Adding to External S3!");
                byte[] imageBytes = imageOperationService.getImage(applicationProperties.getLocal().getBucket(), imageKey);
                imageOperationService.uploadImageFromBytes(externalS3Client, applicationProperties.getExternal().getBucket().getBucketKey(), imageKey, imageBytes);
            } else if (databaseAction == SpecialDatabaseAction.REMOVE_FROM_EXTERNAL) {
                log.info("Removing from External S3!");
                imageOperationService.removeImage(externalS3Client, applicationProperties.getExternal().getBucket().getBucketKey(), imageKey);
            }
        }
        return new ImageInfoDto();
    }

    public String generateUniqueId() {
        String possibleId = ObjectIdGenerator.get();
        for (int i = 0; i < ImageOperationService.MAX_UNIQUE_ID_GENERATE_ATTEMPTS; i++) {
            if (!localImageInfoRepository.existsByImageKey(possibleId)) {
                return possibleId;
            }
            log.warn("Had to try another unique id before uploading!");
        }
        // TODO Do something nicer here.
        throw new IllegalStateException();
    }

}
