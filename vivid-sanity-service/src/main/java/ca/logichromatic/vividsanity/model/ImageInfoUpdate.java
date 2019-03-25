package ca.logichromatic.vividsanity.model;

import lombok.Data;
import lombok.experimental.Accessors;

import java.util.List;


@Data
@Accessors(chain = true)
public class ImageInfoUpdate {
    private String title;
    private String description;
    private List<TagInfo> tags;
    private VisiblityType visibility;
}
