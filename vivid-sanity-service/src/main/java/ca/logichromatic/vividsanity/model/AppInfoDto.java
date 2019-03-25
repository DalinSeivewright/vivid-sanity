package ca.logichromatic.vividsanity.model;


import lombok.Data;
import lombok.experimental.Accessors;

import java.util.List;

@Data
@Accessors(chain = true)
public class AppInfoDto {
    private ServerMode serverMode;
    private List<TagInfo> tags;
}
