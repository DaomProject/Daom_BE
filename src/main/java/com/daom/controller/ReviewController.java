package com.daom.controller;

import com.daom.config.auth.UserDetailsImpl;
import com.daom.domain.Member;
import com.daom.dto.ReviewCreateDto;
import com.daom.dto.ReviewReadDto;
import com.daom.dto.response.RestResponse;
import com.daom.service.ResponseService;
import com.daom.service.ReviewService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;

@RequiredArgsConstructor
@Slf4j
@RequestMapping("/reviews")
@RestController
public class ReviewController {
    private final ReviewService reviewService;
    private final ResponseService responseService;

    @PostMapping("/{shopId}")
    public RestResponse createReview(@AuthenticationPrincipal UserDetailsImpl userDetails,
                                     @PathVariable("shopId") Long shopId,
                                     @RequestPart("review") ReviewCreateDto reviewCreateDto,
                                     @RequestPart(value = "photos", required = false) List<MultipartFile> photos) {
        Member member = userDetails.getMember();

        reviewService.createReview(member, shopId, reviewCreateDto, photos);

        return responseService.getSuccessResponse("리뷰 작성 완료");
    }

    @PutMapping("/{reviewId}")
    public RestResponse updateReview(@AuthenticationPrincipal UserDetailsImpl userDetails,
                                     @PathVariable("reviewId") Long reviewId,
                                     @RequestPart("review") ReviewCreateDto reviewCreateDto,
                                     @RequestPart(value = "photos", required = false) List<MultipartFile> photos) {
        Member member = userDetails.getMember();

        reviewService.updateReview(member, reviewId, reviewCreateDto, photos);
        return responseService.getSuccessResponse("리뷰 수정 완료");
    }

    @DeleteMapping("/{reviewId}")
    public RestResponse deleteReview(@AuthenticationPrincipal UserDetailsImpl userDetails,
                                     @PathVariable("reviewId") Long reviewId) {
        Member member = userDetails.getMember();

        reviewService.deleteReview(member, reviewId);
        return responseService.getSuccessResponse("리뷰 삭제 완료");
    }


    @GetMapping
    public RestResponse readReviews(
            @RequestParam(name = "photo", defaultValue = "false", required = false) Boolean havePhoto,
            @RequestParam(name = "page", defaultValue = "0", required = false) int page,
            @RequestParam(name = "limit", defaultValue = "8", required = false) int limit
    ) {
        List<ReviewReadDto> reviewReadDtos = reviewService.readReviewsByPage(havePhoto, page, limit);
        long totalSize = reviewService.countByHavePhotos(havePhoto);

        return responseService.getPageResponse(reviewReadDtos, (int) totalSize, reviewReadDtos.size(), page);
    }
}
