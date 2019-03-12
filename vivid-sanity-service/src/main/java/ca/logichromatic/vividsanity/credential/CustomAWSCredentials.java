package ca.logichromatic.vividsanity.credential;

import lombok.Getter;

@Getter
public class CustomAWSCredentials {
    private String accessKey;
    private String secretkey;

    public static CustomAWSCredentialsBuilder builder() {
        return new CustomAWSCredentialsBuilder();
    }

    public static class CustomAWSCredentialsBuilder {
        private String accessKey;
        private String secretKey;

        public CustomAWSCredentialsBuilder accessKey(String accessKey) {
            this.accessKey = accessKey;
            return this;
        }

        public CustomAWSCredentialsBuilder secretKey(String secretKey) {
            this.secretKey = secretKey;
            return this;
        }

        public CustomAWSCredentials build() {
            CustomAWSCredentials customCredentials = new CustomAWSCredentials();
            customCredentials.accessKey = this.accessKey;
            customCredentials.secretkey = this.secretKey;
            return customCredentials;
        }
    }
}
