package ca.logichromatic.vividsanity.model;

import lombok.Data;
import lombok.experimental.Accessors;

import java.util.List;

@Data
@Accessors(chain = true)
public class ImageInfo {
    private String imageUri;
    private String thumbnailUri;
    private String description;
    private List<String> tags;
    private VisibilityStatus visibilityStatus;
}
