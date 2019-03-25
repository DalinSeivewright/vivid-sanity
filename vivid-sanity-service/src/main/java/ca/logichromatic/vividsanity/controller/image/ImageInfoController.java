package ca.logichromatic.vividsanity.controller.image;

import ca.logichromatic.vividsanity.configuration.ApplicationProperties;
import ca.logichromatic.vividsanity.exception.ImageNotFoundException;
import ca.logichromatic.vividsanity.model.ImageInfoDto;
import ca.logichromatic.vividsanity.model.ImageInfoUpdate;
import ca.logichromatic.vividsanity.model.ServerMode;
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

    @Autowired
    private ApplicationProperties applicationProperties;

    @GetMapping()
    public List<ImageInfoDto> getImages() {
        return imageService.getImages();
    }

    @GetMapping("/{imageKey}")
    public ImageInfoDto getImage(@PathVariable String imageKey) {
        return imageService.getImage(imageKey);
    }

    @PostMapping
    public ImageInfoDto uploadImage(@RequestParam("file") MultipartFile file) throws IOException {
        if (applicationProperties.getServerMode() == ServerMode.EXTERNAL) {
            throw new ImageNotFoundException();
        }
        return imageService.uploadImage(file, file.getBytes().length);
    }

    @PutMapping("{imageKey}")
    public ImageInfoDto updateImageInfo(@PathVariable String imageKey, @RequestBody ImageInfoUpdate imageInfoUpdate) {
        return imageService.updateImage(imageKey, imageInfoUpdate);
    }
}
