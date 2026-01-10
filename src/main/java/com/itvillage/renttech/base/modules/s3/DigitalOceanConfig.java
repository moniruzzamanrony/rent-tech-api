package com.itvillage.renttech.base.modules.s3;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import software.amazon.awssdk.auth.credentials.AwsBasicCredentials;
import software.amazon.awssdk.auth.credentials.StaticCredentialsProvider;
import software.amazon.awssdk.regions.Region;
import software.amazon.awssdk.services.s3.S3Client;
import software.amazon.awssdk.services.s3.S3Configuration;

import java.net.URI;

@Configuration
public class DigitalOceanConfig {

    @Value("${do.spaces.endpoint}")
    private String endPoint;

    @Value("${do.spaces.access-key}")
    private String accessKey;

    @Value("${do.spaces.secret-key}")
    private String secretKey;

    @Value("${do.spaces.region}")
    private String region;

    @Bean
    public S3Client s3Client() {
        String endpoint = endPoint; // your endpoint
        AwsBasicCredentials credentials = AwsBasicCredentials.create(
                accessKey,
                secretKey
        );

        return S3Client.builder()
                .endpointOverride(URI.create(endpoint))
                .region(Region.of(region)) // your region
                .credentialsProvider(StaticCredentialsProvider.create(credentials))
                .serviceConfiguration(S3Configuration.builder()
                        .pathStyleAccessEnabled(true)
                        .build())
                .build();
    }
}

