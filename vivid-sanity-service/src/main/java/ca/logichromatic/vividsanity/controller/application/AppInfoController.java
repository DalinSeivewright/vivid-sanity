package ca.logichromatic.vividsanity.controller.application;

import ca.logichromatic.vividsanity.model.AppInfoDto;
import ca.logichromatic.vividsanity.service.application.AppInfoService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/info")
public class AppInfoController {

    @Autowired
    private AppInfoService appInfoService;

    @GetMapping()
    public AppInfoDto getInfo() {
        return appInfoService.getInfo();
    }
}
