package ca.logichromatic.vividsanity.transformer;

import ca.logichromatic.vividsanity.controller.proxy.ImageProxyController;
import ca.logichromatic.vividsanity.entity.ImageInfo;
import ca.logichromatic.vividsanity.model.ImageInfoDto;
import ca.logichromatic.vividsanity.util.SimpleSubPath;
import org.springframework.stereotype.Service;

@Service
public class ImageInfoTransformer {

    public ImageInfoDto toDto(ImageInfo imageInfo) {
        return new ImageInfoDto()
                .setImageKey(imageInfo.getImageKey())
                .setDescription(imageInfo.getDescription())
                .setImageUri(buildProxyPath(imageInfo.getImageKey()))
                .setThumbnailUri(buildProxyPath(imageInfo.getImageKey()))
                .setVisibilityStatus(imageInfo.getVisibility());
    }

    private String buildProxyPath(String objectKey) {
        return SimpleSubPath.builder()
                .path(ImageProxyController.IMAGE_PROXY_ENDPOINT)
                .path(objectKey)
                .build()
                .getPath();
    }
}
