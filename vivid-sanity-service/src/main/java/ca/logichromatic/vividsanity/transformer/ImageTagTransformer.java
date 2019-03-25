package ca.logichromatic.vividsanity.transformer;

import ca.logichromatic.vividsanity.entity.ImageTag;
import ca.logichromatic.vividsanity.model.TagInfo;
import org.springframework.stereotype.Service;

import java.util.UUID;

@Service
public class ImageTagTransformer {



    public TagInfo toDto(ImageTag imageTag) {
        return new TagInfo().setName(imageTag.getTag());
    }

    // TODO Handle this properly.  Don't want to do a brand new list every time.
    // Also maybe make this a foreign key into standalone tag table
    // where tags need to be created ahead of time?
    public ImageTag toEntity(UUID imageId, String tag) {
        return ImageTag.newInstance(imageId, tag);
    }
}
