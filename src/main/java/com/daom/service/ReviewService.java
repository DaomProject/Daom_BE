package com.daom.service;

import com.daom.domain.*;
import com.daom.dto.ReviewCreateDto;
import com.daom.dto.ReviewDtosAndCount;
import com.daom.dto.ReviewReadDto;
import com.daom.exception.NoSuchReviewException;
import com.daom.exception.NoSuchShopException;
import com.daom.exception.NotAuthorityThisJobException;
import com.daom.repository.*;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

import java.util.*;
import java.util.stream.Collectors;

@RequiredArgsConstructor
@Transactional(readOnly = true)
@Slf4j
@Service
public class ReviewService {

    private final ShopRepository shopRepository;
    private final ReviewRepository reviewRepository;
    private final UploadFileRepository uploadFileRepository;
    private final TagRepository tagRepository;
    private final FileCloudStorage fileCloudStorage;
    private final StudentLikeUnlikeReviewRepository studentLikeUnlikeReviewRepository;

    @Transactional
    public void createReview(Member loginMember, Long shopId, ReviewCreateDto reviewCreateDto, List<MultipartFile> photos) {
        Shop shop = shopRepository.findById(shopId).orElseThrow(NoSuchShopException::new);

        if (loginMember.getRole() != Role.STUDENT) {
            throw new NotAuthorityThisJobException();
        }
        Student student = loginMember.getStudent();

        Review review = Review.builder()
                .shop(shop)
                .student(student)
                .content(reviewCreateDto.getContent())
                .build();

        // 태그 붙이기 ( 중복 제거 )
        List<String> tagNames = reviewCreateDto.getTags();
        if (tagNames != null && !tagNames.isEmpty()) {
            Set<String> newTagNamesTemp = new HashSet<>(tagNames);
            tagNames = new ArrayList<>(newTagNamesTemp);

            addNewReviewTag(review, tagNames);
        }

        // 사진 저장
        savePhotos(photos, review);

        reviewRepository.save(review);
    }

    @Transactional
    public void updateReview(Member loginMember, Long reviewId, ReviewCreateDto reviewCreateDto, List<MultipartFile> newPhotos) {
        Review review = reviewRepository.findByIdWithFilesAndTags(reviewId).orElseThrow(NoSuchReviewException::new);

        if (loginMember.getRole() != Role.STUDENT || !Objects.equals(review.getStudent().getId(), loginMember.getStudent().getId())) {
            throw new NotAuthorityThisJobException();
        }

        // 내용 변경
        review.updateContent(reviewCreateDto.getContent());

        // 태그 변경
        List<String> newTagNames = reviewCreateDto.getTags();
        List<ReviewTag> originReviewTags = review.getTags();
        List<ReviewTag> deleteReviewTags = new ArrayList<>();


        // -새 태그와 비교해서 지워질 태그 삭제
        if (newTagNames != null && !newTagNames.isEmpty()) { // 새 태그가 존재
            Set<String> newTagNamesTemp = new HashSet<>(newTagNames);
            newTagNames = new ArrayList<>(newTagNamesTemp);
            if (!originReviewTags.isEmpty()) { // 기존 태그가 비어있지 않음
                for (ReviewTag originReviewTag : originReviewTags) {
                    String tagName = originReviewTag.getTag().getName();
                    if (!newTagNames.contains(tagName)) {
                        deleteReviewTags.add(originReviewTag);
                    } else {
                        newTagNames.remove(tagName);
                    }
                }
            }

            for (ReviewTag deleteReviewTag : deleteReviewTags) {
                review.detachReviewTag(deleteReviewTag);
            }

            // 새 태그 생성
            addNewReviewTag(review, newTagNames);
        } else { // 새 태그 비어있음
            if (!originReviewTags.isEmpty()) { // 기존 태그 존재
                review.detachAllReviewTag();
            }
        }

        // 파일 변경 ( 삭제 후 생성 )
        List<ReviewFile> originPhotos = review.getPhotos();
        deleteReviewFile(originPhotos);
        review.getPhotos().clear();
        savePhotos(newPhotos, review);

    }

    // ReviewFile : UploadFile = 1:N 관계를 가지므로 UploadFile을 일일히 삭제해주어야한다.
    private void deleteReviewFile(List<ReviewFile> reviewFiles) {

        if (!reviewFiles.isEmpty()) {
            List<UploadFile> uploadFiles = reviewFiles.stream()
                    .map(ReviewFile::getFile)
                    .collect(Collectors.toList());
            uploadFiles.forEach(uploadFile -> {
                fileCloudStorage.deleteFile(uploadFile.getSavedName());
                uploadFileRepository.delete(uploadFile);
            });
        }
    }

    // 태그 레포지토리를 태그 이름으로 조회 후
    // 있다면 -> 이미 존재하는 태그이므로 num만 1 증가
    // 없다면 -> 존재하지 않는 태그이므로 태그 객체 생성 + 1 증가
    private void addNewReviewTag(Review review, List<String> newTagNames) {
        for (String newTagName : newTagNames) {
            Tag tag = tagRepository.findByName(newTagName).orElse(null);
            if (tag != null) {
                review.getTags().add(new ReviewTag(review, tag));
            } else {
                Tag newTag = new Tag(newTagName);
                tagRepository.save(newTag); // 새 태그 저장
                review.getTags().add(new ReviewTag(review, newTag));
            }
        }
    }

    private void savePhotos(List<MultipartFile> photos, Review review) {
        if (photos != null && !photos.isEmpty()) {
            List<UploadFile> uploadFiles = fileCloudStorage.storeFiles(photos);
            List<ReviewFile> reviewFiles = uploadFiles.stream().map(file -> new ReviewFile(review, file)).collect(Collectors.toList());
            review.getPhotos().addAll(reviewFiles);
            review.updateHavePhotos();
        }
    }

    @Transactional
    public void deleteReview(Member loginMember, Long reviewId) {
        Review review = reviewRepository.findByIdWithFilesAndTags(reviewId).orElseThrow(NoSuchReviewException::new);

        if (loginMember.getRole() != Role.STUDENT || !Objects.equals(review.getStudent().getId(), loginMember.getStudent().getId())) {
            throw new NotAuthorityThisJobException();
        }

        if (!review.getTags().isEmpty()) {
            review.detachAllReviewTag();
        }

        if (!review.getPhotos().isEmpty()) {
            deleteReviewFile(review.getPhotos());
            review.getPhotos().clear();
        }

        reviewRepository.delete(review);
    }

    public void deleteReview(Review review) {
        if (!review.getTags().isEmpty()) {
            review.detachAllReviewTag();
        }

        if (!review.getPhotos().isEmpty()) {
            deleteReviewFile(review.getPhotos());
            review.getPhotos().clear();
        }

        reviewRepository.delete(review);
    }

    public ReviewDtosAndCount readReviewsByPage(Boolean havePhoto, int page, int limit, long shopId) {

        Pageable pageable = PageRequest.of(page, limit);
        int totalSize = 0;
        List<Review> reviews = new ArrayList<>();

        if (shopId != -1) {
            // 해당 상점의 리뷰 검색
            Shop shop = shopRepository.findById(shopId).orElseThrow(NoSuchShopException::new);
            if (!havePhoto) {
                // 글 리뷰
                reviews = reviewRepository.findBypageAndShopWithoutPhotos(pageable, shop);
            } else {
                // 사진 리뷰
                reviews = reviewRepository.findByPageAndShopWithPhotos(pageable, shop);
            }

            totalSize = Math.toIntExact(reviewRepository.countByHavePhotosAndShop(havePhoto, shop));
        } else {
            // 전체 리뷰 검색
            if (!havePhoto) {
                // 글 리뷰
                reviews = reviewRepository.findByPageWithoutPhotos(pageable);
            } else {
                // 사진 리뷰
                reviews = reviewRepository.findByPageWithPhotos(pageable);
            }

            totalSize = Math.toIntExact(reviewRepository.countByHavePhotos(havePhoto));

        }


        List<ReviewReadDto> reviewReadDtoList = reviews.stream().map(Review::toReadDto).collect(Collectors.toList());
        reviewReadDtoList.forEach(this::reviewReadDtoAttachS3Link);

        return new ReviewDtosAndCount(reviewReadDtoList, totalSize);
    }

    // reviewDto에 설정된 이미지 이름들을 링크로 바꿔줌
    private void reviewReadDtoAttachS3Link(ReviewReadDto reviewReadDto) {
        // userthumb 주소 설정
        if (!reviewReadDto.getUserThumbnail().equals("")) {
            String thumbnailSavedName = reviewReadDto.getUserThumbnail();
            String thumbUrl = fileCloudStorage.getUrl(thumbnailSavedName);
            reviewReadDto.setUserThumbnail(thumbUrl);
        }

        List<String> photos = reviewReadDto.getPhotos();
        List<String> photoUrls = photos.stream().map(fileCloudStorage::getUrl).collect(Collectors.toList());
        reviewReadDto.setPhotos(photoUrls);
    }

    @Transactional
    public boolean like(Long reviewId, Student student, boolean isLike) {

        Review review = reviewRepository.findById(reviewId).orElseThrow(NoSuchReviewException::new);
        // StudentLikeUnlikeReview 테이블에 레코드 추가
        StudentLikeUnlikeReview now;
        StudentLikeUnlikeReview before = studentLikeUnlikeReviewRepository.findByStudentAndReview(student, review).orElse(null);
        //  해당 review의 likenum or unlikenum을 갱신
        if (before == null) {
            // 아직 평가하지 않았다면
            now = new StudentLikeUnlikeReview(student, review, isLike);
            studentLikeUnlikeReviewRepository.save(now);
            if (isLike) {
                review.likeNumPlusNum(1);
            } else {
                review.unlikeNumPlusNum(1);
            }
        } else if (before.isLike() ^ isLike) {//평가 내용이 변경되었을 경우 (XOR)
            if (isLike) {
                // 이전 : unlike , 지금 : Like
                review.unlikeNumPlusNum(-1);
                review.likeNumPlusNum(1);
                before.likeTogle();
            } else {
                // 이전 : like , 지금 : unlike
                review.unlikeNumPlusNum(1);
                review.likeNumPlusNum(-1);
                before.likeTogle();
            }
        } else {
            // 같은 평가 2번했을 경우 -> 취소
            if (before.isLike()) {
                review.likeNumPlusNum(-1);
            } else {
                review.unlikeNumPlusNum(-1);
            }

            studentLikeUnlikeReviewRepository.delete(before);
            return false; // 취소만 했으면 false 리턴
        }

        return true; // 변경이 이루어진다면 true 리턴
    }
}
