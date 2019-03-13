package ca.logichromatic.vividsanity.controller.image;

import ca.logichromatic.vividsanity.model.ImageInfo;
import ca.logichromatic.vividsanity.service.image.ImageServiceInterface;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.io.IOException;
import java.util.List;

@RestController
@RequestMapping("/api/images")
public class ImageController {

    @Autowired
    private ImageServiceInterface imageService;

    @GetMapping()
    public List<ImageInfo> getImages() throws IOException {
        return imageService.getImages();
    }
}
