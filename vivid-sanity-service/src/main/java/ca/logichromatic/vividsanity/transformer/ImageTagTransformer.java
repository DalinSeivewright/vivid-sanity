package ca.logichromatic.vividsanity.transformer;

import ca.logichromatic.vividsanity.entity.ImageTag;
import org.springframework.stereotype.Service;

import java.util.UUID;

@Service
public class ImageTagTransformer {

    public String toString(ImageTag imageTag) {
        return imageTag.getTag();
    }

    // TODO Handle this properly.  Don't want to do a brand new list every time.
    // Also maybe make this a foreign key into standalone tag table
    // where tags need to be created ahead of time?
    public ImageTag toEntity(UUID imageId, String tag) {
        return ImageTag.newInstance(imageId, tag);
    }
}
