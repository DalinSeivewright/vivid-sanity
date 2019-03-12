package ca.logichromatic.vividsanity.service;


import ca.logichromatic.vividsanity.configuration.ApplicationProperties;
import ca.logichromatic.vividsanity.credential.CustomAWSCredentials;
import ca.logichromatic.vividsanity.credential.CustomAWSCredentialsProvider;
import ca.logichromatic.vividsanity.exception.ImageNotFoundException;
import ca.logichromatic.vividsanity.model.ImageInfo;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.io.IOUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.web.bind.annotation.ResponseBody;
import software.amazon.awssdk.core.sync.ResponseTransformer;
import software.amazon.awssdk.regions.Region;
import software.amazon.awssdk.services.s3.S3Client;
import software.amazon.awssdk.services.s3.model.*;

import java.io.IOException;
import java.io.InputStream;
import java.net.URI;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Comparator;
import java.util.List;
import java.util.stream.Collectors;

@Slf4j
@Service
public class ImageService {
    @Autowired
    private ApplicationProperties applicationProperties;

    public List<ImageInfo> getImages() throws IOException {
        try {
            URI uri = URI.create(applicationProperties.getMinio().getUri());
            CustomAWSCredentials customAWSCredentials = CustomAWSCredentials.builder()
                    .accessKey(applicationProperties.getMinio().getAccessKey())
                    .secretKey(applicationProperties.getMinio().getSecretKey())
                    .build();
            S3Client s3Client = S3Client.builder()
                    .region(Region.US_EAST_1)
                    .endpointOverride(uri)
                    .credentialsProvider(new CustomAWSCredentialsProvider(customAWSCredentials))
                    .build();


            ListObjectsResponse imageListing  = s3Client.listObjects(ListObjectsRequest.builder().bucket("private").build());
            Comparator<S3Object> s3ObjectComparator = Comparator.comparing(s3Object -> s3Object.lastModified(), Comparator.naturalOrder());
            return imageListing.contents().stream().sorted(s3ObjectComparator).map(object -> toImageInfo(object)).collect(Collectors.toList());
        } catch (NoSuchBucketException bucketException) {
            log.error(bucketException.getMessage());
            throw new ImageNotFoundException();
        } catch (Exception e) {
            log.error(e.getMessage());
            throw new ImageNotFoundException();
        }
    }

    private ImageInfo toImageInfo(S3Object object) {
        return new ImageInfo().setImageUri("/i/" + object.key());
    }
}
