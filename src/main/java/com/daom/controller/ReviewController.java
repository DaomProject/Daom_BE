package com.daom.controller;

import com.daom.config.auth.UserDetailsImpl;
import com.daom.domain.Member;
import com.daom.domain.Student;
import com.daom.domain.StudentVisitShop;
import com.daom.dto.ReviewCreateDto;
import com.daom.dto.ReviewDtosAndCount;
import com.daom.dto.ReviewReadDto;
import com.daom.dto.response.RestResponse;
import com.daom.exception.NotAuthorityThisJobException;
import com.daom.exception.CantReviewThisShopException;
import com.daom.service.ResponseService;
import com.daom.service.ReviewService;
import com.daom.service.VisitService;
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
    public static final int REVIEW_PERIOD = 3; // 리뷰 작성 가능 기간

    private final ReviewService reviewService;
    private final ResponseService responseService;
    private final VisitService visitService;

    @PostMapping("/{shopId}")
    public RestResponse createReview(@AuthenticationPrincipal UserDetailsImpl userDetails,
                                     @PathVariable("shopId") Long shopId,
                                     @RequestPart("review") ReviewCreateDto reviewCreateDto,
                                     @RequestPart(value = "photos", required = false) List<MultipartFile> photos) {
        Member member = userDetails.getMember();

        // 해당 상점을 최근에 3일 이내에 방문 했을 시에만 리뷰 작성 가능
        List<StudentVisitShop> visitInPeriod = visitService.getVisitCanBeReviewed(member, shopId, REVIEW_PERIOD);
        if(!visitInPeriod.isEmpty()){
            reviewService.createReview(member, shopId, reviewCreateDto, photos);
            visitInPeriod.get(0).review(); // 가장 오래된 방문 기록에서 리뷰 체크하도록 작성
        }else{
            throw new CantReviewThisShopException();
        }

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

    @PostMapping("/{reviewId}/like")
    public RestResponse likeReview(@AuthenticationPrincipal UserDetailsImpl userDetails,
                                   @PathVariable("reviewId") Long reviewId) {
        Student student = userDetails.getMember().getStudent();
        if (student == null) {
            throw new NotAuthorityThisJobException(); // 학생 계정만 리뷰에대한 좋아요, 싫어요 할 수 있다.
        }
        boolean done = reviewService.like(reviewId, student, true);
        if (!done) {
            return responseService.getSuccessResponse("리뷰 좋아요 취소");
        }

        return responseService.getSuccessResponse("리뷰 좋아요 완료");
    }

    @PostMapping("/{reviewId}/unlike")
    public RestResponse unlikeReview(@AuthenticationPrincipal UserDetailsImpl userDetails,
                                     @PathVariable("reviewId") Long reviewId) {
        Student student = userDetails.getMember().getStudent();
        if (student == null) {
            throw new NotAuthorityThisJobException(); // 학생 계정만 리뷰에대한 좋아요, 싫어요 할 수 있다.
        }
        boolean done = reviewService.like(reviewId, student, false);
        if (!done) {
            return responseService.getSuccessResponse("리뷰 싫어요 취소");
        }

        return responseService.getSuccessResponse("리뷰 싫어요 완료");
    }

    @GetMapping
    public RestResponse readReviews(
            @RequestParam(name = "photo", defaultValue = "false", required = false) Boolean havePhoto,
            @RequestParam(name = "page", defaultValue = "0", required = false) int page,
            @RequestParam(name = "limit", defaultValue = "8", required = false) int limit,
            @RequestParam(name = "shopId", defaultValue = "-1", required = false) long shopId
    ) {
        // reviewCount + List<reviewReadDto>
        ReviewDtosAndCount reviewDtosAndCount = reviewService.readReviewsByPage(havePhoto, page, limit, shopId);
        List<ReviewReadDto> reviewDtos = reviewDtosAndCount.getReviewDtos();
        int totalSize = reviewDtosAndCount.getTotalSize();
        return responseService.getPageResponse(reviewDtos, totalSize, reviewDtos.size(), page);
    }
}
