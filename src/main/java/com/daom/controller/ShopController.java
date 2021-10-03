package com.daom.controller;

import com.daom.config.auth.UserDetailsImpl;
import com.daom.domain.Member;
import com.daom.dto.ShopAndMenuFilesDto;
import com.daom.dto.ShopCreateDto;
import com.daom.dto.ShopReadDto;
import com.daom.dto.response.RestResponse;
import com.daom.exception.MenuIndexAndFileNotMatchException;
import com.daom.service.ResponseService;
import com.daom.service.ShopService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;

@RequiredArgsConstructor
@Slf4j
@RequestMapping("/shops")
@RestController
public class ShopController {
    private final ShopService shopService;
    private final ResponseService responseService;

    @GetMapping("/myshop")
    public RestResponse readMyShop(
            @AuthenticationPrincipal UserDetailsImpl userDetails
    ){
        Member member = userDetails.getMember();
        List<ShopReadDto> shopReadDtos = shopService.readMyShop(member);
        return responseService.getPageResponse(shopReadDtos, shopReadDtos.size());
    }

    @PostMapping
    public RestResponse create(
            @AuthenticationPrincipal UserDetailsImpl userDetails,
            @RequestPart("shop") ShopCreateDto shopCreateDto,
            @RequestPart(value = "thumbnail", required = false) MultipartFile thumbnail,
            @RequestPart(value = "menufiles", required = false) List<MultipartFile> menuFiles,
            @RequestParam(name = "index", required = false) List<Integer> menuHavingFileIndex) {

        Member member = userDetails.getMember();

        if (menuFiles != null && menuFiles.size() != menuHavingFileIndex.size()) {
            throw new MenuIndexAndFileNotMatchException();
        }

        ShopAndMenuFilesDto shopAndMenuFilesDto = ShopAndMenuFilesDto.builder()
                .thumbnail(thumbnail)
                .menuFiles(menuFiles)
                .menuHavingFileIndexes(menuHavingFileIndex)
                .build();
        shopService.createShop(member, shopCreateDto, shopAndMenuFilesDto);

        return responseService.getSuccessResponse("Shop 등록 완료");

    }

    @DeleteMapping("/{id}")
    public RestResponse delete(
            @AuthenticationPrincipal UserDetailsImpl userDetails, @PathVariable Long id) {

        Long loginMemberId = userDetails.getMember().getId();

        shopService.deleteShop(loginMemberId, id);
        return responseService.getSuccessResponse("Shop 삭제 완료");
    }

    @PutMapping("/{id}")
    public RestResponse update(
            @AuthenticationPrincipal UserDetailsImpl userDetails,
            @PathVariable("id") Long shopId,
            @RequestPart("shop") ShopCreateDto shopCreateDto,
            @RequestPart(value = "thumbnail", required = false) MultipartFile thumbnail,
            @RequestPart(value = "menufiles", required = false) List<MultipartFile> menuFiles,
            @RequestParam(name = "index", required = false) List<Integer> menuHavingFileIndex
    ) {
        Long loginMemberId = userDetails.getMember().getId();

        ShopAndMenuFilesDto shopAndMenuFilesDto = ShopAndMenuFilesDto.builder()
                .thumbnail(thumbnail)
                .menuFiles(menuFiles)
                .menuHavingFileIndexes(menuHavingFileIndex)
                .build();

        shopService.updateShop(loginMemberId, shopId, shopCreateDto, shopAndMenuFilesDto);

        return responseService.getSuccessResponse("Shop 수정 완료");
    }

}