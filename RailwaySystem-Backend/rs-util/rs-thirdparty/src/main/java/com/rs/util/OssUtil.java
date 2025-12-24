package com.rs.util;

import com.aliyun.oss.ClientException;
import com.aliyun.oss.OSS;
import com.aliyun.oss.OSSException;
import com.aliyun.oss.model.PutObjectRequest;
import com.rs.enums.RespCode;
import com.rs.exception.CommonException;
import com.rs.properties.AliOssProperties;
import lombok.RequiredArgsConstructor;
import org.springframework.boot.autoconfigure.condition.ConditionalOnBean;
import org.springframework.stereotype.Component;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.io.InputStream;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.UUID;

@Component
@RequiredArgsConstructor
@ConditionalOnBean(OSS.class)
public class OssUtil {

    private final OSS ossClient;

    private final AliOssProperties aliOssProperties;

    public String upload(MultipartFile file) {

        try {
            // 构建上传对象请求
            InputStream inputStream = file.getInputStream();
            String oldName = file.getOriginalFilename();
            int lastIndexOf = 0;
            if (oldName != null) {
                lastIndexOf = oldName.lastIndexOf(".");
            }
            String fileName = oldName.substring(0, lastIndexOf) + "-" + UUID.randomUUID().toString().replaceAll("-", "") + oldName.substring(lastIndexOf);
            String dir = LocalDate.now().format(DateTimeFormatter.ofPattern("yyyyMMdd"));
            fileName=dir+"/"+fileName;
            PutObjectRequest putObjectRequest = new PutObjectRequest(aliOssProperties.getBucketName(), fileName, inputStream);
            ossClient.putObject(putObjectRequest);
            String url=String.format("https://%s.%s/%s",aliOssProperties.getBucketName(),aliOssProperties.getEndpoint(),fileName);
            System.out.println("上传的文件路径："+url);
            return url;
        } catch (OSSException | ClientException | IOException e) {
            throw new CommonException(RespCode.ERROR, "文件上传失败");
        }
    }
}
