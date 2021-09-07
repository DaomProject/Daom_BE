package com.daom.service;

import com.daom.domain.UploadFile;
import com.daom.exception.FileStoreException;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

@Slf4j
@Service
public class FileStorage {

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

        String originFilename = multipartFile.getOriginalFilename();
        String storeFileName = createStoreFileName(originFilename);
        long size = multipartFile.getSize();
        String ext = extractExt(originFilename);
        log.info(getFullPath(storeFileName));

        try {
            multipartFile.transferTo(new File(getFullPath(storeFileName)));
        } catch (IOException e) {
            throw new FileStoreException();
        }
        return UploadFile.builder()
                .savedName(storeFileName)
                .originName(originFilename)
                .size(size)
                .extension(ext).build();
    }

    public boolean deleteFile(String savedFileName){
        File file = new File(getFullPath(savedFileName));

        if(file.exists()){
            if(file.delete()){
                log.info("파일 삭제 : "+ savedFileName);
                return true;
            }else{
                log.warn("파일 삭제 실패 : " +savedFileName);
            }
        }
        else{
            log.warn("파일이 존재하지않습니다.");
        }

        return false;
    }

    private String createStoreFileName(String originalFilename) {
        String ext = extractExt(originalFilename);
        String uuid = UUID.randomUUID().toString();
        return uuid + "." + ext;
    }

    // 확장자를 떼냄
    private String extractExt(String originalFilename) {
        if(originalFilename != null){
            int pos = originalFilename.lastIndexOf(".");
            return originalFilename.substring(pos + 1);
        }

        return null;
    }


    public File getFile(String filename){
        return new File(getFullPath(filename));
    }
}