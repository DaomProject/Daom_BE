package com.daom.controller;

import com.daom.config.auth.UserDetailsImpl;
import com.daom.domain.Member;
import com.daom.domain.Student;
import com.daom.dto.*;
import com.daom.dto.response.RestResponse;
import com.daom.exception.MenuIndexAndFileNotMatchException;
import com.daom.exception.NotAuthorityThisJobException;
import com.daom.exception.NotInsertSortedMethodException;
import com.daom.service.LikeService;
import com.daom.service.ResponseService;
import com.daom.service.ShopService;
import com.daom.service.ZzimService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.util.ArrayList;
import java.util.List;

@RequiredArgsConstructor
@Slf4j
@RequestMapping("/shops")
@RestController
public class ShopController {
    private final ShopService shopService;
    private final ResponseService responseService;
    private final ZzimService zzimService;
    private final LikeService likeService;

    @GetMapping("/myshop")
    public RestResponse readMyShop(
            @AuthenticationPrincipal UserDetailsImpl userDetails
    ){
        Member member = userDetails.getMember();
        List<ShopReadDto> shopReadDtos = shopService.readMyShops(member);
        return responseService.getPageResponse(shopReadDtos, shopReadDtos.size());
    }

    @GetMapping("/{id}")
    public RestResponse readShop(
            @PathVariable(value = "id") Long shopId
    ){
        ShopReadDto shopReadDto = shopService.readShop(shopId);
        return responseService.getSingleResponse(shopReadDto);
    }

    @GetMapping
    public RestResponse readShopPagesByDistance(
            @RequestParam(value = "sort", defaultValue = "distance", required = false) String sort, // distance, reviewnum, likenum
            @RequestParam(value = "page", defaultValue = "0", required = false) int page,
            @RequestParam(value = "limit", defaultValue = "8", required = false) int limit,
            @RequestParam(value = "lat", defaultValue = "35.8467367", required = false) double latitude, // ?????? default ????????? ?????????
            @RequestParam(value = "lon", defaultValue = "127.1271732,17", required = false) double longitude, // ?????? default ????????? ?????????
            @RequestParam(value = "dist", defaultValue = "10", required = false ) double distance // ???????????? ????????? ???????????? ????????????? ( km ?????? )
    ){
        ShopDtosAndCount shopDtosAndCount = null;

        if(sort.equals("distance")){
            shopDtosAndCount = shopService.readSimpleShopsSortedByDistance(page, limit, distance, latitude, longitude);
        } else if(sort.equals("likenum")){
            shopDtosAndCount = shopService.readSimpleShopsSortedByLikeNum(page, limit, distance, latitude, longitude);
        }else if(sort.equals("reviewnum")){
            shopDtosAndCount = shopService.readSimpleShopsSortedByReviewNum(page, limit, distance, latitude, longitude);
        }else{
            throw new NotInsertSortedMethodException();
        }
        List<ShopSimpleDto> shopSimpleDtos = shopDtosAndCount.getShopSimpleDtos();
        int totalSize = shopDtosAndCount.getTotalSize();
        //TODO Count ??????
        return responseService.getPageResponse(shopSimpleDtos, totalSize, shopSimpleDtos.size(), page);
    }

    @PostMapping
    public RestResponse create(
            @AuthenticationPrincipal UserDetailsImpl userDetails,
            @RequestPart("shop") ShopCreateDto shopCreateDto,
            @RequestPart(value = "thumbnail", required = false) MultipartFile thumbnail,
            @RequestPart(value = "menufiles", required = false) List<MultipartFile> menuFiles,
            @RequestParam(value = "index", required = false) List<Integer> menuHavingFileIndex) {

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

        return responseService.getSuccessResponse("Shop ?????? ??????");

    }

    @DeleteMapping("/{id}")
    public RestResponse delete(
            @AuthenticationPrincipal UserDetailsImpl userDetails, @PathVariable Long id) {

        Long loginMemberId = userDetails.getMember().getId();

        shopService.deleteShop(loginMemberId, id);
        return responseService.getSuccessResponse("Shop ?????? ??????");
    }

    @PutMapping("/{id}")
    public RestResponse update(
            @AuthenticationPrincipal UserDetailsImpl userDetails,
            @PathVariable("id") Long shopId,
            @RequestPart("shop") ShopCreateDto shopCreateDto,
            @RequestPart(value = "thumbnail", required = false) MultipartFile thumbnail,
            @RequestPart(value = "menufiles", required = false) List<MultipartFile> menuFiles,
            @RequestParam(value = "index", required = false) List<Integer> menuHavingFileIndex
    ) {

        if(menuHavingFileIndex != null && menuFiles != null){
            if (menuFiles.size() != menuHavingFileIndex.size()) {
                throw new MenuIndexAndFileNotMatchException();
            }
        }
        Long loginMemberId = userDetails.getMember().getId();

        ShopAndMenuFilesDto shopAndMenuFilesDto = ShopAndMenuFilesDto.builder()
                .thumbnail(thumbnail)
                .menuFiles(menuFiles)
                .menuHavingFileIndexes(menuHavingFileIndex)
                .build();

        shopService.updateShop(loginMemberId, shopId, shopCreateDto, shopAndMenuFilesDto);

        return responseService.getSuccessResponse("Shop ?????? ??????");
    }

    @PostMapping("/{id}/zzim")
    public RestResponse zzim(
            @AuthenticationPrincipal UserDetailsImpl userDetails,
            @PathVariable("id") Long shopId
    ){
        Student student = userDetails.getMember().getStudent();
        if(student == null){
            throw new NotAuthorityThisJobException();
        }
        Long studentId = student.getId();
        zzimService.saveZzim(studentId, shopId);

        return responseService.getSuccessResponse();
    }

    @DeleteMapping("/{id}/zzim")
    public RestResponse deleteZzim(
            @AuthenticationPrincipal UserDetailsImpl userDetails,
            @PathVariable("id") Long shopId
    ){
        Student student = userDetails.getMember().getStudent();
        if(student == null) {
            throw new NotAuthorityThisJobException();
        }
        Long studentId = userDetails.getMember().getStudent().getId();
        zzimService.deleteZzim(studentId, shopId);

        return responseService.getSuccessResponse();
    }

    @PostMapping("/{id}/like")
    public RestResponse like(
            @AuthenticationPrincipal UserDetailsImpl userDetails,
            @PathVariable("id") Long shopId
    ){
        Student student = userDetails.getMember().getStudent();
        if(student == null){
            throw new NotAuthorityThisJobException();
        }
        Long studentId = student.getId();
        likeService.like(studentId, shopId);

        return responseService.getSuccessResponse();
    }

    @PostMapping("/{id}/unlike")
    public RestResponse unLike(
            @AuthenticationPrincipal UserDetailsImpl userDetails,
            @PathVariable("id") Long shopId
    ){
        Student student = userDetails.getMember().getStudent();
        if(student == null){
            throw new NotAuthorityThisJobException();
        }
        Long studentId = student.getId();
        likeService.unLike(studentId, shopId);

        return responseService.getSuccessResponse();
    }

}