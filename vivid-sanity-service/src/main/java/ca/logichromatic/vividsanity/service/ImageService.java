package ca.logichromatic.vividsanity.service;


import ca.logichromatic.vividsanity.credential.CustomAWSCredentials;
import ca.logichromatic.vividsanity.credential.CustomAWSCredentialsProvider;
import ca.logichromatic.vividsanity.configuration.ApplicationProperties;
import org.apache.commons.io.IOUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import software.amazon.awssdk.auth.signer.internal.SignerConstant;
import software.amazon.awssdk.core.ResponseBytes;
import software.amazon.awssdk.core.client.config.ClientOverrideConfiguration;
import software.amazon.awssdk.core.client.config.SdkAdvancedClientOption;
import software.amazon.awssdk.core.sync.ResponseTransformer;
import software.amazon.awssdk.http.apache.ApacheHttpClient;
import software.amazon.awssdk.regions.Region;
import software.amazon.awssdk.services.s3.S3Client;
import software.amazon.awssdk.services.s3.model.GetObjectRequest;

import javax.imageio.stream.ImageInputStream;
import java.io.*;
import java.net.URI;
import java.util.Base64;

@Service
public class ImageService {
    @Autowired
    private ApplicationProperties applicationProperties;
    public String getImage(String imageId) throws IOException {
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
        return Base64.getEncoder().encodeToString(IOUtils.toByteArray(imageInputStream));
    }

    public ResponseBytes getImageAsStream(String imageId) {

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

        ResponseBytes bytes = s3Client.getObject(GetObjectRequest.builder().bucket("private").key(imageId).build(),
                    ResponseTransformer.toBytes());


        return bytes;
    }
}
