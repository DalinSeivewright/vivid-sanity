package ca.logichromatic.vividsanity.service.application;


import ca.logichromatic.vividsanity.configuration.ApplicationProperties;
import ca.logichromatic.vividsanity.model.AppInfo;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Slf4j
@Service
public class AppInfoService {
    @Autowired
    private ApplicationProperties applicationProperties;

    public AppInfo getInfo() {
        return new AppInfo().setServerMode(applicationProperties.getServerMode());
    }

}
