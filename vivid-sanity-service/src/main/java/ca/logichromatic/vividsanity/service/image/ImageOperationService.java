package ca.logichromatic.vividsanity.service.image;

import ca.logichromatic.vividsanity.configuration.ApplicationProperties;
import ca.logichromatic.vividsanity.credential.CustomAWSCredentials;
import ca.logichromatic.vividsanity.credential.CustomAWSCredentialsProvider;
import ca.logichromatic.vividsanity.exception.ImageNotFoundException;
import ca.logichromatic.vividsanity.model.ImageInfo;
import ca.logichromatic.vividsanity.util.ObjectIdGenerator;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.io.IOUtils;
import org.springframework.stereotype.Service;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.multipart.MultipartFile;
import software.amazon.awssdk.core.sync.RequestBody;
import software.amazon.awssdk.core.sync.ResponseTransformer;
import software.amazon.awssdk.regions.Region;
import software.amazon.awssdk.services.s3.S3Client;
import software.amazon.awssdk.services.s3.model.*;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.net.URI;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.stream.Collectors;

@Slf4j
@Service
public class ImageOperationService {
    private static final int MAX_UNIQUE_ID_GENERATE_ATTEMPTS = 5;

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

    public ImageInfo uploadImage(ApplicationProperties.BucketProperties bucketProperties, InputStream inputStream, int byteLength) {
        try {
            S3Client s3Client = buildClient(bucketProperties);
            return uploadObject(s3Client, bucketProperties.getBucketKey(), inputStream, byteLength);
        } catch (Exception e) {
            log.error("error");
        }
        return null;
    }

    private ImageInfo uploadObject(S3Client s3Client, String bucketKey, InputStream inputStream, int byteLength) throws IOException {
        String newObjectId = generateUniqueId(s3Client, bucketKey);
        PutObjectRequest request = PutObjectRequest.builder().bucket(bucketKey).key(newObjectId).build();
        RequestBody requestBody = RequestBody.fromInputStream(inputStream, byteLength);
        PutObjectResponse response = s3Client.putObject(request, requestBody);
        return new ImageInfo().setImageUri("/i/" + newObjectId);
    }

    private String generateUniqueId(S3Client s3Client, String bucketKey) {
        String possibleId = ObjectIdGenerator.get();
        for (int i = 0; i < MAX_UNIQUE_ID_GENERATE_ATTEMPTS; i++) {
            try {
                HeadObjectRequest request = HeadObjectRequest.builder().bucket(bucketKey).key(possibleId).build();
                // Unfortunately this call throws an exception if the key does not exist
                // I would prefer a nicer method like "existsObject" that simply returned a boolean.
                s3Client.headObject(request);
            } catch (NoSuchKeyException noKeyException) {
                return possibleId;
            }
            log.warn("Had to try another unique id before uploading!");
        }
        // TODO Do something nicer here.
        throw new IllegalStateException();
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
