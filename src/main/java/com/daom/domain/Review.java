package com.daom.domain;

import com.daom.dto.ReviewCreateDto;
import com.daom.dto.ReviewReadDto;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import javax.persistence.*;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

@Entity
@Getter
@NoArgsConstructor
public class Review extends BaseTimeEntity {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "review_id")
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "shop_id")
    private Shop shop;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "student_id")
    private Student student;

    @Column(nullable = false)
    private String content;

    @Column(nullable = false, name = "like_num")
    private int like;

    @Column(nullable = false, name = "unlike_num")
    private int unlike;

    @Column(nullable = false, name = "have_photos")
    private boolean havePhotos;

    @OneToMany(mappedBy = "review", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<ReviewTag> tags = new ArrayList<>();

    @OneToMany(mappedBy = "review", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<ReviewFile> photos = new ArrayList<>();

    @OneToMany(mappedBy = "review", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<StudentLikeUnlikeReview> studentLikeUnlikeReviews = new ArrayList<>();

    @Builder
    public Review(Shop shop, Student student, String content) {
        this.shop = shop;
        this.student = student;
        this.content = content;
        this.like = 0;
        this.unlike = 0;
        this.havePhotos = false;
    }

    public void likeNumPlusNum(int num){
        like += num;
    }

    public void unlikeNumPlusNum(int num){
        unlike += num;
    }

    public void updateContent(String content) {
        this.content = content;
    }

    public void updateHavePhotos() {
        if (photos.isEmpty()) {
            this.havePhotos = false;
        } else {
            this.havePhotos = true;
        }
    }

    public void detachReviewTag(ReviewTag deletedReviewTag) {
        this.tags.remove(deletedReviewTag);
        deletedReviewTag.getTag().minusTagNum(1);
    }

    public void detachAllReviewTag() {
        this.tags.forEach(t -> t.getTag().minusTagNum(1));
        this.tags.clear();
    }

    public ReviewReadDto toReadDto() {

        List<String> tagNames = new ArrayList<>();
        tags.forEach(t -> tagNames.add(t.getTag().getName()));
        String thumbNames = "";
        List<String> photoNames = new ArrayList<>();

        if(student.getThumbnail() != null){
            thumbNames = student.getThumbnail().getSavedName();
        }

        if (havePhotos) {
            photos.forEach(p -> {
                photoNames.add(p.getFile().getSavedName());
            });
        }

        return ReviewReadDto.builder()
                .id(id)
                .nickname(student.getMember().getNickname())
                .level(student.getLevel())
                .userThumbnail(thumbNames)
                .shopId(shop.getId())
                .shopName(shop.getName())
                .shopLocDesc(shop.getLocDesc() + shop.getLocDetailDesc())
                .content(content)
                .like(like)
                .unlike(unlike)
                .tags(tagNames)
                .photos(photoNames)
                .build();

    }
}
