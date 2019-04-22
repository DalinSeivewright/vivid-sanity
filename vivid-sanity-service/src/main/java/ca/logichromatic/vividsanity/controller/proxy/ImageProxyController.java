package ca.logichromatic.vividsanity.controller.proxy;

import ca.logichromatic.vividsanity.configuration.ApplicationProperties;
import ca.logichromatic.vividsanity.service.proxy.ImageProxyService;
import com.sun.xml.internal.ws.client.sei.ResponseBuilder;
import javafx.application.Application;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;


@RestController
@RequestMapping(ImageProxyController.IMAGE_PROXY_ENDPOINT)
public class ImageProxyController {

    public static final String IMAGE_PROXY_ENDPOINT = "/i";

    @Autowired
    private ImageProxyService imageProxyService;

    @Autowired
    private ApplicationProperties applicationProperties;

    @ResponseBody
    @GetMapping(value = "/{imageId}", produces = MediaType.IMAGE_JPEG_VALUE)
    public ResponseEntity<byte[]> getImage(@PathVariable String imageId) {
        byte[] imageBytes =  imageProxyService.getImage(imageId);
        HttpHeaders headers = new HttpHeaders();
        headers.add("Cache-Control", "max-age=" + applicationProperties.getCacheMaxAge());
        return new ResponseEntity<>(imageBytes, headers, HttpStatus.OK);
    }
}
