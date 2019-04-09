package ca.logichromatic.vividsanity.service.image;


import ca.logichromatic.vividsanity.configuration.ApplicationProperties;
import ca.logichromatic.vividsanity.entity.ImageInfo;
import ca.logichromatic.vividsanity.exception.ImageNotFoundException;
import ca.logichromatic.vividsanity.model.ImageInfoDto;
import ca.logichromatic.vividsanity.model.ImageInfoUpdate;
import ca.logichromatic.vividsanity.model.VisiblityType;
import ca.logichromatic.vividsanity.repository.local.LocalImageInfoRepository;
import ca.logichromatic.vividsanity.transformer.ImageInfoTransformer;
import ca.logichromatic.vividsanity.type.SpecialDatabaseAction;
import ca.logichromatic.vividsanity.util.ImageInputStream;
import ca.logichromatic.vividsanity.util.ObjectIdGenerator;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;
import software.amazon.awssdk.services.s3.S3Client;

import java.awt.*;
import java.awt.image.BufferedImage;
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

    @Autowired
    private ImageInfoTransformer imageInfoTransformer;

    @Autowired
    private ImageManipulationService imageManipulationService;

    public static String THUMBNAIL_SUFFIX = "_thumb";

    public List<ImageInfoDto> getImages() {
        return imageOperationService.getImages();
    }

    public ImageInfoDto getImage(String imageKey) {
        if (imageKey == null || imageKey.length() != ObjectIdGenerator.getObjectKeyLength()) {
            throw new ImageNotFoundException();
        }
        return imageOperationService.getImageInfo(imageKey);
    }


    public List<ImageInfoDto> getSimilarImages(String imageKey) {
        return imageOperationService.getSimilarImages(imageKey);
    }

    public ImageInfoDto uploadImage(MultipartFile multipartFile, int byteSize) throws IOException {
        String imageKey = generateUniqueId();

        BufferedImage originalImage = imageManipulationService.getImage(multipartFile.getInputStream());
        BufferedImage thumbnailImage = imageManipulationService.getThumbnail(originalImage);
        List<Color> palette = imageManipulationService.getPalette(thumbnailImage);
        String paletteString = palette.stream()
                .map(color -> String.format("#%02X%02X%02X", color.getRed(), color.getGreen(), color.getBlue()))
                .collect(Collectors.joining(";"));
        log.info(paletteString);
        ImageInfo imageInfo = ImageInfo.newInstance(imageKey).setVisibility(VisiblityType.PRIVATE).setPalette(paletteString);
        imagePersistenceService.save(imageInfo, SpecialDatabaseAction.NONE);

        S3Client localS3Client = imageOperationService.buildClient(applicationProperties.getLocal().getBucket());
        imageOperationService.uploadImage(localS3Client, applicationProperties.getLocal().getBucket().getBucketKey(), imageKey, ImageInputStream.create(originalImage));
        imageOperationService.uploadImage(localS3Client, applicationProperties.getLocal().getBucket().getBucketKey(), imageKey + THUMBNAIL_SUFFIX, ImageInputStream.create(thumbnailImage));
        if (imageInfo.getVisibility() == VisiblityType.PUBLIC) {
            log.info("Visiblity public!");
            S3Client externalS3Client = imageOperationService.buildClient(applicationProperties.getExternal().getBucket());
            imageOperationService.uploadImage(externalS3Client, applicationProperties.getExternal().getBucket().getBucketKey(), imageKey, ImageInputStream.create(originalImage));
            imageOperationService.uploadImage(externalS3Client, applicationProperties.getExternal().getBucket().getBucketKey(), imageKey + THUMBNAIL_SUFFIX, ImageInputStream.create(thumbnailImage));
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
            if (imageInfo.getVisibility() == VisiblityType.PRIVATE && imageInfoUpdate.getVisibility() == VisiblityType.PUBLIC) {
                databaseAction = SpecialDatabaseAction.ADD_TO_EXTERNAL;
            } else {
                databaseAction = SpecialDatabaseAction.REMOVE_FROM_EXTERNAL;
            }
        }
        log.info("Determined DB Action");
        log.info(databaseAction.name());

        imageInfo = imageInfoTransformer.toEntity(imageInfo, imageInfoUpdate);
        ImageInfo updatedImageInfo = imagePersistenceService.save(imageInfo, databaseAction);

        if (databaseAction != SpecialDatabaseAction.NONE) {
            S3Client externalS3Client = imageOperationService.buildClient(applicationProperties.getExternal().getBucket());
            if (databaseAction == SpecialDatabaseAction.ADD_TO_EXTERNAL) {
                log.info("Adding to External S3!");
                byte[] imageBytes = imageOperationService.getImageBytesFromS3(applicationProperties.getLocal().getBucket(), imageKey);
                imageOperationService.uploadImageFromBytes(externalS3Client, applicationProperties.getExternal().getBucket().getBucketKey(), imageKey, imageBytes);
                byte[] thumbImageBytes = imageOperationService.getImageBytesFromS3(applicationProperties.getLocal().getBucket(), imageKey + THUMBNAIL_SUFFIX);
                imageOperationService.uploadImageFromBytes(externalS3Client, applicationProperties.getExternal().getBucket().getBucketKey(), imageKey + THUMBNAIL_SUFFIX, thumbImageBytes);
            } else if (databaseAction == SpecialDatabaseAction.REMOVE_FROM_EXTERNAL) {
                log.info("Removing from External S3!");
                imageOperationService.removeImage(externalS3Client, applicationProperties.getExternal().getBucket().getBucketKey(), imageKey);
                imageOperationService.removeImage(externalS3Client, applicationProperties.getExternal().getBucket().getBucketKey(), imageKey + THUMBNAIL_SUFFIX);
            }
        }
        return imageInfoTransformer.toDto(updatedImageInfo);
    }

    public void deleteImage(String imageKey) {
        ImageInfo imageInfo = imagePersistenceService.find(imageKey);
        if (imageInfo == null) {
            throw new ImageNotFoundException();
        }
        SpecialDatabaseAction databaseAction = imageInfo.getVisibility() == VisiblityType.PRIVATE ? SpecialDatabaseAction.NONE : SpecialDatabaseAction.REMOVE_FROM_EXTERNAL;

        S3Client localS3Client = imageOperationService.buildClient(applicationProperties.getLocal().getBucket());
        imagePersistenceService.delete(imageInfo, databaseAction);
        imageOperationService.removeImage(localS3Client, applicationProperties.getLocal().getBucket().getBucketKey(), imageKey);
        imageOperationService.removeImage(localS3Client, applicationProperties.getLocal().getBucket().getBucketKey(), imageKey + THUMBNAIL_SUFFIX);
        if (databaseAction == SpecialDatabaseAction.REMOVE_FROM_EXTERNAL) {
            S3Client externalS3Client = imageOperationService.buildClient(applicationProperties.getExternal().getBucket());
            log.info("Removing from External S3!");
            imageOperationService.removeImage(externalS3Client, applicationProperties.getExternal().getBucket().getBucketKey(), imageKey);
            imageOperationService.removeImage(externalS3Client, applicationProperties.getExternal().getBucket().getBucketKey(), imageKey + THUMBNAIL_SUFFIX);
        }
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
