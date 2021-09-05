package com.daom.dto;

import lombok.Builder;
import lombok.Data;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;

@Data
public class ShopAndMenuFilesDto {
    private MultipartFile thumbnail;
    private List<MultipartFile> menuFiles;
    private List<Integer> menuHavingFileIndexes;

    @Builder
    public ShopAndMenuFilesDto(MultipartFile thumbnail, List<MultipartFile> menuFiles, List<Integer> menuHavingFileIndexes) {
        this.thumbnail = thumbnail;
        this.menuFiles = menuFiles;
        this.menuHavingFileIndexes = menuHavingFileIndexes;
    }
}

