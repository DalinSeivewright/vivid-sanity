package ca.logichromatic.vividsanity.credential;

import ca.logichromatic.vividsanity.credential.CustomAWSCredentials;
import software.amazon.awssdk.auth.credentials.AwsCredentials;
import software.amazon.awssdk.auth.credentials.AwsCredentialsProvider;

public class CustomAWSCredentialsProvider implements AwsCredentialsProvider {

    private CustomAWSCredentials customAWSCredentials;

    public CustomAWSCredentialsProvider(CustomAWSCredentials customAWSCredentials) {
        this.customAWSCredentials = customAWSCredentials;
    }

    @Override
    public AwsCredentials resolveCredentials() {
        return new AwsCredentials() {
            @Override
            public String accessKeyId() {
                return customAWSCredentials.getAccessKey();
            }

            @Override
            public String secretAccessKey() {
                return customAWSCredentials.getSecretkey();
            }
        };
    }
}
