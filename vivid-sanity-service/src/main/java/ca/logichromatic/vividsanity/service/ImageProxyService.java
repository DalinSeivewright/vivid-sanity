package ca.logichromatic.vividsanity.service;


import ca.logichromatic.vividsanity.credential.CustomAWSCredentials;
import ca.logichromatic.vividsanity.credential.CustomAWSCredentialsProvider;
import ca.logichromatic.vividsanity.configuration.ApplicationProperties;
import ca.logichromatic.vividsanity.exception.ImageNotFoundException;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.io.IOUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.web.bind.annotation.ResponseBody;
import software.amazon.awssdk.core.sync.ResponseTransformer;
import software.amazon.awssdk.regions.Region;
import software.amazon.awssdk.services.s3.S3Client;
import software.amazon.awssdk.services.s3.model.GetObjectRequest;
import software.amazon.awssdk.services.s3.model.NoSuchKeyException;

import java.io.*;
import java.net.URI;
@Slf4j
@Service
public class ImageProxyService {
    @Autowired
    private ApplicationProperties applicationProperties;

    public @ResponseBody byte[] getImage(String imageId) throws IOException {
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


            InputStream imageInputStream = s3Client.getObject(GetObjectRequest.builder().bucket("private").key(imageId).build(),
                    ResponseTransformer.toInputStream());


            return IOUtils.toByteArray(imageInputStream);
        } catch (NoSuchKeyException keyException) {
            throw new ImageNotFoundException();
        } catch (Exception e) {
            log.error(e.getMessage());
            throw new ImageNotFoundException();
        }
    }
}
