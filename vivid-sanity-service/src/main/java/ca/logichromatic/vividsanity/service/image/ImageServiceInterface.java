package ca.logichromatic.vividsanity.service.image;

import ca.logichromatic.vividsanity.model.ImageInfoDto;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.io.InputStream;
import java.util.List;

public interface ImageServiceInterface {
    List<ImageInfoDto> getImages();
    ImageInfoDto uploadImage(MultipartFile multipartFile, int byteSize)  throws IOException;
}
