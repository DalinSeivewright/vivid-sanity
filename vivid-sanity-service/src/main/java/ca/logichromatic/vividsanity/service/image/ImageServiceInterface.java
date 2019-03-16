package ca.logichromatic.vividsanity.service.image;

import ca.logichromatic.vividsanity.model.ImageInfoDto;

import java.io.InputStream;
import java.util.List;

public interface ImageServiceInterface {
    List<ImageInfoDto> getImages();
    ImageInfoDto uploadImage(InputStream fileStream, int byteSize);
}
