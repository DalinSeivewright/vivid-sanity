package ca.logichromatic.vividsanity.controller.image;

import ca.logichromatic.vividsanity.model.ImageInfo;
import ca.logichromatic.vividsanity.service.image.ImageServiceInterface;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

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

    @PostMapping
    public ImageInfo uploadImage(@RequestParam("file") MultipartFile file) throws IOException {
        return imageService.uploadImage(file.getInputStream(), file.getBytes().length);
    }
}
