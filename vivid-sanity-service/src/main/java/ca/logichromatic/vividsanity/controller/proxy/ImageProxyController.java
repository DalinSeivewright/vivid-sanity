package ca.logichromatic.vividsanity.controller.proxy;

import ca.logichromatic.vividsanity.service.proxy.ImageProxyServiceInterface;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping(ImageProxyController.IMAGE_PROXY_ENDPOINT)
public class ImageProxyController {

    public static final String IMAGE_PROXY_ENDPOINT = "/i";

    @Autowired
    private ImageProxyServiceInterface imageProxyServiceInterface;

    @ResponseBody
    @GetMapping(value = "/{imageId}", produces = MediaType.IMAGE_JPEG_VALUE)
    public byte[] getImage(@PathVariable String imageId) {
        return imageProxyServiceInterface.getImage(imageId);
    }
}
