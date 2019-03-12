package ca.logichromatic.vividsanity.controller;

import ca.logichromatic.vividsanity.service.ImageService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.io.IOException;

@RestController
@RequestMapping("/api/image")
public class ImageController {

    @Autowired
    private ImageService imageService;

    @GetMapping("/{imageId}")
    public String getImage(@PathVariable String imageId) throws IOException {
        return imageService.getImage(imageId);
    }
}
