package ca.logichromatic.vividsanity.service.application;


import ca.logichromatic.vividsanity.configuration.ApplicationProperties;
import ca.logichromatic.vividsanity.model.AppInfoDto;
import ca.logichromatic.vividsanity.model.TagInfo;
import ca.logichromatic.vividsanity.repository.local.LocalImageInfoRepository;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.stream.Collectors;

@Slf4j
@Service
public class AppInfoService {
    @Autowired
    private ApplicationProperties applicationProperties;

    @Autowired
    private LocalImageInfoRepository localImageInfoRepository;

    public AppInfoDto getInfo() {
        return new AppInfoDto()
                .setServerMode(applicationProperties.getServerMode())
                .setTags(localImageInfoRepository.findAllDistinctTags()
                        .stream()
                        .map(tagName -> new TagInfo().setName(tagName))
                        .collect(Collectors.toList()));
    }

}
