package ca.logichromatic.vividsanity.transformer;

import ca.logichromatic.vividsanity.controller.proxy.ImageProxyController;
import ca.logichromatic.vividsanity.entity.ImageInfo;
import ca.logichromatic.vividsanity.model.ImageInfoDto;
import ca.logichromatic.vividsanity.model.ImageInfoUpdate;
import ca.logichromatic.vividsanity.service.image.ImagePersistenceService;
import ca.logichromatic.vividsanity.service.image.ImageService;
import ca.logichromatic.vividsanity.util.SimpleSubPath;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.stream.Collectors;

@Service
public class ImageInfoTransformer {
    @Autowired
    private ImageTagTransformer imageTagTransformer;

    public ImageInfoDto toDto(ImageInfo imageInfo) {
        return new ImageInfoDto()
                .setImageKey(imageInfo.getImageKey())
                .setTitle(imageInfo.getTitle())
                .setDescription(imageInfo.getDescription())
                .setImageUri(buildProxyPath(imageInfo.getImageKey()))
                .setThumbnailUri(buildProxyPath(imageInfo.getImageKey() + ImageService.THUMBNAIL_SUFFIX))
                .setVisibility(imageInfo.getVisibility())
                .setTags(imageInfo.getTags().stream().map(tag -> imageTagTransformer.toDto(tag)).collect(Collectors.toList()))
                .setPalette(imageInfo.getPalette());
    }

    public ImageInfo toEntity(ImageInfo imageInfo, ImageInfoUpdate imageInfoUpdate) {
        return imageInfo.setTitle(imageInfo.getTitle())
                .setDescription(imageInfoUpdate.getDescription())
                .setTags(imageInfoUpdate.getTags().stream().map(tag -> imageTagTransformer.toEntity(imageInfo.getIdentifier(), tag.getName())).collect(Collectors.toList()))
                .setVisibility(imageInfoUpdate.getVisibility());
    }

    private String buildProxyPath(String objectKey) {
        return SimpleSubPath.builder()
                .path(ImageProxyController.IMAGE_PROXY_ENDPOINT)
                .path(objectKey)
                .build()
                .getPath();
    }
}
