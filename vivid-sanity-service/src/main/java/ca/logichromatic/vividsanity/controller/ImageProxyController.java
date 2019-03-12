package ca.logichromatic.vividsanity.controller;

import ca.logichromatic.vividsanity.service.ImageProxyService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.io.IOException;

@RestController
@RequestMapping("/i")
public class ImageProxyController {

    @Autowired
    private ImageProxyService imageService;

    @GetMapping(value = "/{imageId}", produces = MediaType.IMAGE_JPEG_VALUE)
    public byte[] getImage(@PathVariable String imageId) throws IOException {
        return imageService.getImage(imageId);
    }
}
