package com.daom.service;

import com.amazonaws.services.s3.model.DeleteObjectRequest;
import com.amazonaws.services.s3.model.ObjectMetadata;
import com.daom.domain.UploadFile;
import com.daom.exception.FileStoreException;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

@Slf4j
@RequiredArgsConstructor
@Service
public class FileCloudStorage {

    private final S3Service s3Service;

    @Value("${file.dir}")
    private String fileDir;

    public String getFullPath(String filename) {
        return fileDir + filename;
    }

    // 여러 파일을 저장하고 파일 객체들을 생성해줌
    public List<UploadFile> storeFiles(List<MultipartFile> multipartFiles) {
        List<UploadFile> storeFileResult = new ArrayList<>();

        for (MultipartFile multipartFile : multipartFiles) {
            if (!multipartFile.isEmpty()) {
                storeFileResult.add(storeFile(multipartFile));
            }
        }
        return storeFileResult;
    }

    // 실제 파일을 저장하고 파일 객체를 생성해줌
    public UploadFile storeFile(MultipartFile multipartFile) {
        if (multipartFile.isEmpty()) {
            return null;
        }

        // s3 저장용 메타데이터
        ObjectMetadata objectMetadata = new ObjectMetadata();

        String originFilename = multipartFile.getOriginalFilename();
        String storeFileName = createStoreFileName(originFilename);
        long size = multipartFile.getSize();
        String ext = extractExt(originFilename);

        objectMetadata.setContentLength(size);
        objectMetadata.setContentType(multipartFile.getContentType());

        try (InputStream inputStream = multipartFile.getInputStream()) {
            s3Service.upload(inputStream, objectMetadata, storeFileName);
        } catch (IOException e) {
            throw new FileStoreException();
        }
        return UploadFile.builder()
                .savedName(storeFileName)
                .originName(originFilename)
                .size(size)
                .extension(ext).build();
    }

    public void deleteFile(String savedFileName) {
        s3Service.delete(savedFileName);
    }

    private String createStoreFileName(String originalFilename) {
        String ext = extractExt(originalFilename);
        String uuid = UUID.randomUUID().toString();
        return uuid + "." + ext;
    }

    // 확장자를 떼냄
    private String extractExt(String originalFilename) {
        if (originalFilename != null) {
            int pos = originalFilename.lastIndexOf(".");
            return originalFilename.substring(pos + 1);
        }

        return null;
    }

    public String getUrl(String fileName) {
        return s3Service.getUrl(fileName);
    }
}