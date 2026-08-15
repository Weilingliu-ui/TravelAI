package com.travelai.travelai.controller;

import com.travelai.travelai.common.response.Result;
import com.travelai.travelai.config.MinioConfig;
import io.minio.MinioClient;
import io.minio.PutObjectArgs;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.annotation.Resource;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.util.Map;
import java.util.UUID;

@Tag(name = "文件管理", description = "MinIO 文件上传")
@RestController
@RequestMapping("/api/files")
public class FileController {

    @Autowired(required = false)
    private MinioClient minioClient;

    @Resource
    private MinioConfig minioConfig;

    @Operation(summary = "上传文件")
    @PostMapping("/upload")
    public Result<Map<String, String>> upload(@RequestParam("file") MultipartFile file) {
        try {
            String ext = getExtension(file.getOriginalFilename());
            String objectName = "travel/" + UUID.randomUUID().toString().replace("-", "") + ext;

            minioClient.putObject(PutObjectArgs.builder()
                    .bucket(minioConfig.getBucket())
                    .object(objectName)
                    .stream(file.getInputStream(), file.getSize(), -1)
                    .contentType(file.getContentType())
                    .build());

            String url = minioConfig.getEndpoint() + "/" + minioConfig.getBucket() + "/" + objectName;
            return Result.success(Map.of("url", url));
        } catch (Exception e) {
            return Result.error(500, "上传失败: " + e.getMessage());
        }
    }

    private String getExtension(String filename) {
        if (filename == null || !filename.contains(".")) return "";
        return filename.substring(filename.lastIndexOf("."));
    }
}
