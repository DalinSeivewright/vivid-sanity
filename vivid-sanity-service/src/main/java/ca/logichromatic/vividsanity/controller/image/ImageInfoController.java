package ca.logichromatic.vividsanity.controller.image;

import ca.logichromatic.vividsanity.model.ImageInfoDto;
import ca.logichromatic.vividsanity.service.image.ImageService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.List;

@RestController
@RequestMapping("/api/images")
public class ImageInfoController {

    @Autowired
    private ImageService imageService;

    @GetMapping()
    public List<ImageInfoDto> getImages() {
        return imageService.getImages();
    }

    @PostMapping
    public ImageInfoDto uploadImage(@RequestParam("file") MultipartFile file) throws IOException {
        return imageService.uploadImage(file, file.getBytes().length);
    }
}