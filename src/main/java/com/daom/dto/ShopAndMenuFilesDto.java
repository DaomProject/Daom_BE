package com.daom.dto;

import lombok.Data;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;

@Data
public class ShopAndMenuFilesDto {
    private MultipartFile thumbnail;
    private List<MultipartFile> menuFiles;
    private List<Integer> haveFileMenuIndex;
}

