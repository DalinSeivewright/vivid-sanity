package ca.logichromatic.vividsanity.model;

import lombok.Data;
import lombok.experimental.Accessors;

import java.util.List;

@Data
@Accessors(chain = true)
public class ImageInfoDto {
    private String imageKey;
    private String imageUri;
    private String thumbnailUri;
    private String description;
    private List<String> tags;
    private String palette;
    private VisibilityStatus visibilityStatus;
}
