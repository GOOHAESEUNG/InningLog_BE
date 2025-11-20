package com.inninglog.inninglog.global.s3;

import lombok.Getter;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;
import org.springframework.stereotype.Service;

@Getter
@Component
@Service
public class S3UrlProperties {

    private final String bucket;
    private final String region;
    private final String baseUrl;

    public S3UrlProperties(
            @Value("${cloud.aws.s3.bucket}") String bucket,
            @Value("${cloud.aws.region.static}") String region
    ) {
        this.bucket = bucket;
        this.region = region;
        this.baseUrl = "https://" + bucket + ".s3." + region + ".amazonaws.com";
    }
}
