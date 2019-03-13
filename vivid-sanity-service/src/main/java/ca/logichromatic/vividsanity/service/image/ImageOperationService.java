package ca.logichromatic.vividsanity.service.image;

import ca.logichromatic.vividsanity.configuration.ApplicationProperties;
import ca.logichromatic.vividsanity.credential.CustomAWSCredentials;
import ca.logichromatic.vividsanity.credential.CustomAWSCredentialsProvider;
import ca.logichromatic.vividsanity.exception.ImageNotFoundException;
import ca.logichromatic.vividsanity.model.ImageInfo;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.io.IOUtils;
import org.springframework.stereotype.Service;
import org.springframework.web.bind.annotation.ResponseBody;
import software.amazon.awssdk.core.sync.ResponseTransformer;
import software.amazon.awssdk.regions.Region;
import software.amazon.awssdk.services.s3.S3Client;
import software.amazon.awssdk.services.s3.model.*;

import java.io.InputStream;
import java.net.URI;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.stream.Collectors;

@Slf4j
@Service
public class ImageOperationService {
    public List<ImageInfo> getImages(ApplicationProperties.BucketProperties bucketProperties) {
        if (bucketProperties == null) {
            log.warn("Null Bucket Properties passed in.  Returning empty array.");
            return new ArrayList<>();
        }

        try {
            S3Client s3Client = buildClient(bucketProperties);
            ListObjectsResponse imageListing  = getBucketListing(s3Client, bucketProperties.getBucketKey());
            Comparator<S3Object> s3ObjectComparator = Comparator.comparing(s3Object -> s3Object.lastModified(), Comparator.naturalOrder());
            return imageListing.contents().stream()
                    .sorted(s3ObjectComparator)
                    .map(object -> toImageInfo(object))
                    .collect(Collectors.toList());
        } catch (NoSuchBucketException bucketException) {
            log.error(bucketException.getMessage());
        } catch (Exception e) {
            log.error(e.getMessage());
        }
        return new ArrayList<>();
    }


    public byte[] getImage(ApplicationProperties.BucketProperties bucketProperties, String imageId){
        try {
            S3Client s3Client = buildClient(bucketProperties);
            InputStream imageInputStream = getObjectAsInputStream(s3Client, bucketProperties.getBucketKey(), imageId);
            return IOUtils.toByteArray(imageInputStream);
        } catch (NoSuchKeyException keyException) {
            throw new ImageNotFoundException();
        } catch (Exception e) {
            log.error(e.getMessage());
            throw new ImageNotFoundException();
        }
    }


    private InputStream getObjectAsInputStream(S3Client s3Client, String bucketKey, String imageId) {
        GetObjectRequest request = GetObjectRequest.builder().bucket(bucketKey).key(imageId).build();
        return s3Client.getObject(request, ResponseTransformer.toInputStream());
    }

    private ListObjectsResponse getBucketListing(S3Client s3Client, String bucketKey) {
        ListObjectsRequest request = ListObjectsRequest.builder().bucket(bucketKey).build();
        return s3Client.listObjects(request);
    }

    private S3Client buildClient(ApplicationProperties.BucketProperties bucketProperties) {
        URI targetEndpointOverride = URI.create(bucketProperties.getUri());
        CustomAWSCredentials customAWSCredentials = CustomAWSCredentials.builder()
                .accessKey(bucketProperties.getAccessKey())
                .secretKey(bucketProperties.getSecretKey())
                .build();
        S3Client s3Client = S3Client.builder()
                .region(Region.US_EAST_1)
                .endpointOverride(targetEndpointOverride)
                .credentialsProvider(new CustomAWSCredentialsProvider(customAWSCredentials))
                .build();
        return s3Client;
    }

    private ImageInfo toImageInfo(S3Object object) {
        return new ImageInfo().setImageUri("/i/" + object.key());
    }

}
