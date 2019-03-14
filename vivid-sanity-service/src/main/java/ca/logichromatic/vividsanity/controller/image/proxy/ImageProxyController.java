package ca.logichromatic.vividsanity.controller.image.proxy;

import ca.logichromatic.vividsanity.service.image.proxy.ImageProxyServiceInterface;
import ca.logichromatic.vividsanity.service.image.proxy.PublicImageProxyService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.*;

import java.io.IOException;

@RestController
@RequestMapping("/i")
public class ImageProxyController {

    @Autowired
    private ImageProxyServiceInterface imageProxyServiceInterface;

    @ResponseBody
    @GetMapping(value = "/{imageId}", produces = MediaType.IMAGE_JPEG_VALUE)
    public byte[] getImage(@PathVariable String imageId) {
        return imageProxyServiceInterface.getImage(imageId);
    }
}
