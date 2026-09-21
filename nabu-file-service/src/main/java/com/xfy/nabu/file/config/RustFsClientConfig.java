package com.xfy.nabu.file.config;

import java.net.URI;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import software.amazon.awssdk.auth.credentials.AwsBasicCredentials;
import software.amazon.awssdk.auth.credentials.StaticCredentialsProvider;
import software.amazon.awssdk.http.urlconnection.UrlConnectionHttpClient;
import software.amazon.awssdk.regions.Region;
import software.amazon.awssdk.services.s3.S3Client;
import software.amazon.awssdk.services.s3.S3Configuration;
import software.amazon.awssdk.services.s3.presigner.S3Presigner;

/**
 * 通过标准 AWS S3 SDK 访问 RustFS，业务代码不依赖任何 RustFS 专属 SDK，
 * 未来切换到真实 AWS S3 / MinIO / 阿里云 OSS 时，只需要改这里的 endpoint/credentials 配置，
 * FileBizService 业务逻辑零改动。
 */
@Configuration
@EnableConfigurationProperties(RustFsProperties.class)
public class RustFsClientConfig {

    /**
     * pathStyleAccessEnabled(true) 必须开启：RustFS/MinIO 类 S3 兼容存储默认按
     * "endpoint/bucket/key" 的 path-style 寻址，而不是 AWS 官方的 "bucket.endpoint/key" virtual-host-style。
     */
    private S3Configuration serviceConfiguration() {
        return S3Configuration.builder().pathStyleAccessEnabled(true).build();
    }

    private StaticCredentialsProvider credentialsProvider(RustFsProperties props) {
        return StaticCredentialsProvider.create(AwsBasicCredentials.create(props.getAccessKey(), props.getSecretKey()));
    }

    @Bean
    public S3Client s3Client(RustFsProperties props) {
        return S3Client.builder()
                .endpointOverride(URI.create(props.getEndpoint()))
                .region(Region.of(props.getRegion()))
                .credentialsProvider(credentialsProvider(props))
                .serviceConfiguration(serviceConfiguration())
                .httpClient(UrlConnectionHttpClient.builder().build())
                .build();
    }

    @Bean
    public S3Presigner s3Presigner(RustFsProperties props) {
        return S3Presigner.builder()
                .endpointOverride(URI.create(props.getEndpoint()))
                .region(Region.of(props.getRegion()))
                .credentialsProvider(credentialsProvider(props))
                .serviceConfiguration(serviceConfiguration())
                .build();
    }
}
