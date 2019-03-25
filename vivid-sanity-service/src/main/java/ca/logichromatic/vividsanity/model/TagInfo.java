package ca.logichromatic.vividsanity.model;

import lombok.Data;
import lombok.experimental.Accessors;

@Data
@Accessors(chain=true)
public class TagInfo {
    private String name;
    private String description;
}
