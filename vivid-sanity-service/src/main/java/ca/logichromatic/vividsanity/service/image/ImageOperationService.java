package ca.logichromatic.vividsanity.service.image;

import ca.logichromatic.vividsanity.configuration.ApplicationProperties;
import ca.logichromatic.vividsanity.credential.CustomAWSCredentials;
import ca.logichromatic.vividsanity.credential.CustomAWSCredentialsProvider;
import ca.logichromatic.vividsanity.exception.ImageNotFoundException;
import ca.logichromatic.vividsanity.model.ImageInfoDto;
import ca.logichromatic.vividsanity.repository.external.ExternalImageInfoRepository;
import ca.logichromatic.vividsanity.repository.local.LocalImageInfoRepository;
import ca.logichromatic.vividsanity.transformer.ImageInfoTransformer;
import ca.logichromatic.vividsanity.util.ObjectIdGenerator;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.io.IOUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import software.amazon.awssdk.core.sync.RequestBody;
import software.amazon.awssdk.core.sync.ResponseTransformer;
import software.amazon.awssdk.regions.Region;
import software.amazon.awssdk.services.s3.S3Client;
import software.amazon.awssdk.services.s3.model.*;

import java.io.InputStream;
import java.net.URI;
import java.util.List;
import java.util.stream.Collectors;

@Slf4j
@Service
@Transactional
public class ImageOperationService {
    public static final int MAX_UNIQUE_ID_GENERATE_ATTEMPTS = 5;

    @Autowired
    private LocalImageInfoRepository localImageInfoRepository;

    @Autowired(required = false)
    private ExternalImageInfoRepository externalImageInfoRepository;


    @Autowired
    private ImageInfoTransformer imageInfoTransformer;

    public List<ImageInfoDto> getImages() {
        return localImageInfoRepository.findAll().stream()
                .map(imageInfo -> imageInfoTransformer.toDto(imageInfo))
                .collect(Collectors.toList());
    }


    public byte[] getImage(ApplicationProperties.BucketProperties bucketProperties, String imageId){
        try {
            S3Client s3Client = buildClient(bucketProperties);
            InputStream imageInputStream = getObjectAsInputStream(s3Client, bucketProperties.getBucketKey(), imageId);
            return IOUtils.toByteArray(imageInputStream);
        } catch (NoSuchKeyException keyException) {
            keyException.printStackTrace();
            throw new ImageNotFoundException();
        } catch (Exception e) {
            e.printStackTrace();
            System.out.println(e);
            log.error(e.getMessage());
            throw new ImageNotFoundException();
        }
    }

    public void uploadImage(S3Client s3Client, String bucketKey, String objectKey, InputStream inputStream, int byteLength) {
        PutObjectRequest request = PutObjectRequest.builder()
                .bucket(bucketKey)
                .key(objectKey)
                .build();
        RequestBody requestBody = RequestBody.fromInputStream(inputStream, byteLength);
        s3Client.putObject(request, requestBody);
    }

    public void uploadImageFromBytes(S3Client s3Client, String bucketKey, String objectKey, byte[] inputBytes) {
        PutObjectRequest request = PutObjectRequest.builder()
                .bucket(bucketKey)
                .key(objectKey)
                .build();
        RequestBody requestBody = RequestBody.fromBytes(inputBytes);
        s3Client.putObject(request, requestBody);
    }

    public void removeImage(S3Client s3Client, String bucketKey, String objectKey) {
        DeleteObjectRequest request = DeleteObjectRequest.builder()
                .bucket(bucketKey)
                .key(objectKey)
                .build();
        s3Client.deleteObject(request);

    }

    public String generateUniqueId(S3Client s3Client, String bucketKey) {
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

    public S3Client buildClient(ApplicationProperties.BucketProperties bucketProperties) {
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
}
