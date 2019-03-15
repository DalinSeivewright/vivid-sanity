package ca.logichromatic.vividsanity.configuration.converter;

import ca.logichromatic.vividsanity.model.ServerMode;
import org.springframework.boot.context.properties.ConfigurationPropertiesBinding;
import org.springframework.core.convert.converter.Converter;
import org.springframework.stereotype.Component;

@Component
@ConfigurationPropertiesBinding
public class ServerModeConverter implements Converter<String, ServerMode> {
    @Override
    public ServerMode convert(String source) {
        if (source == null) {
            return null;
        }

        return ServerMode.valueOf(source.toUpperCase());
    }
}
