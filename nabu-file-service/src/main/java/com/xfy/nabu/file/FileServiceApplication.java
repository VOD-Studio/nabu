package com.xfy.nabu.file;

import org.mybatis.spring.annotation.MapperScan;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

/**
 * 文件服务启动类。职责：对接 RustFS（S3 兼容 API）完成头像/帖子图片/附件等文件的上传、
 * 下载、预签名直传，文件元数据落库。
 * 对外通过 Dubbo 暴露 {@link com.xfy.nabu.api.file.service.FileService}。
 */
@SpringBootApplication(scanBasePackages = {"com.xfy.nabu.file", "com.xfy.nabu.common"})
@MapperScan("com.xfy.nabu.file.mapper")
public class FileServiceApplication {

    public static void main(String[] args) {
        SpringApplication.run(FileServiceApplication.class, args);
    }
}
