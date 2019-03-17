package ca.logichromatic.vividsanity.service.image;


import ca.logichromatic.vividsanity.configuration.ApplicationProperties;
import ca.logichromatic.vividsanity.entity.ImageInfo;
import ca.logichromatic.vividsanity.model.ImageInfoDto;
import ca.logichromatic.vividsanity.model.VisibilityStatus;
import ca.logichromatic.vividsanity.repository.external.ExternalImageInfoRepository;
import ca.logichromatic.vividsanity.repository.local.LocalImageInfoRepository;
import ca.logichromatic.vividsanity.transformer.ImageInfoTransformer;
import ca.logichromatic.vividsanity.type.DatabaseTarget;
import ca.logichromatic.vividsanity.util.ObjectIdGenerator;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;
import software.amazon.awssdk.services.s3.S3Client;
import software.amazon.awssdk.services.s3.model.HeadObjectRequest;
import software.amazon.awssdk.services.s3.model.NoSuchKeyException;

import java.io.IOException;
import java.io.InputStream;
import java.util.List;

@Slf4j
@Service
@ConditionalOnProperty(prefix="vivid", name="serverMode", havingValue = "local")
@Transactional
public class PrivateImageService implements ImageServiceInterface {
    @Autowired
    private ApplicationProperties applicationProperties;

    @Autowired
    private ImageOperationService imageOperationService;

    @Autowired
    private ImageInfoDbService imageInfoDbService;

    @Autowired(required = false)
    private LocalImageInfoRepository localImageInfoRepository;

    @Autowired
    private ExternalImageInfoRepository externalImageInfoRepository;

    @Autowired
    private ImageInfoTransformer imageInfoTransformer;

    public List<ImageInfoDto> getImages() {
        log.error("I am the private bean!");
        return imageOperationService.getImages(DatabaseTarget.LOCAL);
    }

    @Override
    public ImageInfoDto uploadImage(MultipartFile multipartFile, int byteSize) throws IOException {
        log.error("I am private upload bean");
        String imageKey = generateUniqueId();
        ImageInfo imageInfo = ImageInfo.newInstance(imageKey).setDescription("Test Description.").setVisibility(VisibilityStatus.PUBLIC);
        localImageInfoRepository.save(imageInfo);
        if (imageInfo.getVisibility() == VisibilityStatus.PUBLIC) {
            log.info("Saving to external db");
            externalImageInfoRepository.save(imageInfo);
        }

        S3Client localS3Client = imageOperationService.buildClient(applicationProperties.getLocal().getBucket());
        imageOperationService.uploadImage(localS3Client, applicationProperties.getLocal().getBucket().getBucketKey(), imageKey, multipartFile.getInputStream(), byteSize);
        if (imageInfo.getVisibility() == VisibilityStatus.PUBLIC) {
            log.info("Visiblity public!");
            S3Client externalS3Client = imageOperationService.buildClient(applicationProperties.getExternal().getBucket());
            imageOperationService.uploadImage(externalS3Client, applicationProperties.getExternal().getBucket().getBucketKey(), imageKey, multipartFile.getInputStream(), byteSize);
        }
        return imageInfoTransformer.toDto(imageInfo);
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
