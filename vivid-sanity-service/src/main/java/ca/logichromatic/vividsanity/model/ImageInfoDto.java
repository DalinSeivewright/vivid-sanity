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
    private String title;
    private String description;
    private List<TagInfo> tags;
    private String palette;
    private VisiblityType visibility;
}
