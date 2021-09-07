package com.daom.controller;

import com.daom.service.FileStorage;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.util.FileCopyUtils;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;

@RequiredArgsConstructor
@Slf4j
@RequestMapping("/files")
@RestController
public class FileController {

    private final FileStorage fileStorage;

    @GetMapping
    public ResponseEntity<byte[]> getImage(String fileName) throws IOException {

        File file = new File(fileStorage.getFullPath(fileName));

        ResponseEntity<byte[]> result = null;

        log.info("getImage() ... " + fileName);
        HttpHeaders header = new HttpHeaders();
        header.add("Content-type", Files.probeContentType(file.toPath()));
        byte[] imgBytes = FileCopyUtils.copyToByteArray(file);

        result = new ResponseEntity<>(imgBytes, header, HttpStatus.OK);
        return result;

    }
}
