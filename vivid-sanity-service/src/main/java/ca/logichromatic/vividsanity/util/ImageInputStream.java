package ca.logichromatic.vividsanity.util;

import lombok.Getter;

import javax.imageio.ImageIO;
import java.awt.image.BufferedImage;
import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;

@Getter
public class ImageInputStream {
    private static String IMAGE_FORMAT = "png";
    private InputStream inputStream;
    private int bytes;

    public static ImageInputStream create(BufferedImage image) {
        try {
            ImageInputStream imageInputStream = new ImageInputStream();
            ByteArrayOutputStream byteStream = new ByteArrayOutputStream();
            ImageIO.write(image, IMAGE_FORMAT, byteStream);
            imageInputStream.bytes = byteStream.size();
            imageInputStream.inputStream = new ByteArrayInputStream(byteStream.toByteArray(), 0, byteStream.size());
        } catch (IOException e) {
        }
        return null;
    }
}
