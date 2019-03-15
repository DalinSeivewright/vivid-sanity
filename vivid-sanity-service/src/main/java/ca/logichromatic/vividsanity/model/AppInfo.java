package ca.logichromatic.vividsanity.model;


import lombok.Data;
import lombok.experimental.Accessors;

@Data
@Accessors(chain = true)
public class AppInfo {
    private ServerMode serverMode;
}
