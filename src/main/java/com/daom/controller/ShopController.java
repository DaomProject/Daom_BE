package com.daom.controller;

import com.daom.config.auth.UserDetailsImpl;
import com.daom.domain.Member;
import com.daom.dto.ShopAndMenuFilesDto;
import com.daom.dto.ShopCreateDto;
import com.daom.dto.response.RestResponse;
import com.daom.exception.MenuIndexAndFileNotMatchException;
import com.daom.service.FileStorage;
import com.daom.service.ResponseService;
import com.daom.service.ShopService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.stereotype.Controller;
import org.springframework.util.FileCopyUtils;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.util.Base64;
import java.util.List;

@RequiredArgsConstructor
@Slf4j
@RequestMapping("/shops")
@RestController
public class ShopController {
    private final ShopService shopService;
    private final ResponseService responseService;

    @PostMapping
    public RestResponse create(
            @AuthenticationPrincipal UserDetailsImpl userDetails,
            @RequestPart("shop") ShopCreateDto shopCreateDto,
            @RequestPart("thumbnail") MultipartFile thumbnail,
            @RequestPart("menufiles") List<MultipartFile> menuFiles,
            @RequestParam(name = "index",required = false) List<Integer> menuHavingFileIndex) {

        Member member = userDetails.getMember();
        if (menuFiles.size() != menuHavingFileIndex.size()) {
            throw new MenuIndexAndFileNotMatchException();
        }

        ShopAndMenuFilesDto shopAndMenuFilesDto = ShopAndMenuFilesDto.builder()
                .thumbnail(thumbnail)
                .menuFiles(menuFiles)
                .menuHavingFileIndexes(menuHavingFileIndex)
                .build();
        shopService.createShop(member, shopCreateDto, shopAndMenuFilesDto);

        return responseService.getSuccessResponse();

    }
}