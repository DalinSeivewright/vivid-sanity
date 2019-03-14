package ca.logichromatic.vividsanity.service.image;

import ca.logichromatic.vividsanity.model.ImageInfo;

import java.io.InputStream;
import java.util.List;

public interface ImageServiceInterface {
    List<ImageInfo> getImages();
    ImageInfo uploadImage(InputStream fileStream, int byteSize);
}
