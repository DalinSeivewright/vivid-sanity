package ca.logichromatic.vividsanity.service.application;


import ca.logichromatic.vividsanity.configuration.ApplicationProperties;
import ca.logichromatic.vividsanity.model.AppInfoDto;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Slf4j
@Service
public class AppInfoService {
    @Autowired
    private ApplicationProperties applicationProperties;

    public AppInfoDto getInfo() {
        return new AppInfoDto().setServerMode(applicationProperties.getServerMode());
    }

}
