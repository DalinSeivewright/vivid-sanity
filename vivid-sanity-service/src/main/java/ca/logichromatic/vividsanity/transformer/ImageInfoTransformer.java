package ca.logichromatic.vividsanity.transformer;

import ca.logichromatic.vividsanity.controller.proxy.ImageProxyController;
import ca.logichromatic.vividsanity.entity.ImageInfo;
import ca.logichromatic.vividsanity.model.ImageInfoDto;
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
                .setDescription(imageInfo.getDescription())
                .setImageUri(buildProxyPath(imageInfo.getImageKey()))
                .setThumbnailUri(buildProxyPath(imageInfo.getImageKey()))
                .setVisibilityStatus(imageInfo.getVisibility())
                .setTags(imageInfo.getTags().stream().map(tag -> imageTagTransformer.toString(tag)).collect(Collectors.toList()));
    }

    private String buildProxyPath(String objectKey) {
        return SimpleSubPath.builder()
                .path(ImageProxyController.IMAGE_PROXY_ENDPOINT)
                .path(objectKey)
                .build()
                .getPath();
    }
}
