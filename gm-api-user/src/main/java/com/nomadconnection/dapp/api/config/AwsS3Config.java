package com.nomadconnection.dapp.api.config;

import lombok.Data;
import lombok.experimental.Accessors;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Configuration;

@Data
@Accessors(fluent = true)
@Configuration
@ConfigurationProperties(prefix = "s3")
public class AwsS3Config {

    private String bucketUrl = "https://s3.ap-northeast-2.amazonaws.com/stg-mycard.gowid.com/";
    private String bucketName = "stg-mycard.gowid.com";

    //
    //	detour: @Accessors(fluent = true)
    //
    public void setBucketUrl(String bucketUrl) {
        this.bucketUrl = bucketUrl;
    }

    public void setBucketName(String bucketName) {
        this.bucketName = bucketName;
    }
}
