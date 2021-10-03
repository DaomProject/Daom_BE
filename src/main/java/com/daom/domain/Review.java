package com.daom.domain;

import com.daom.dto.ReviewCreateDto;
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
    private Long like;

    @Column(nullable = false, name = "unlike_num")
    private Long unlike;

    @Column(nullable = false, name = "have_photos")
    private boolean havePhotos;

    @OneToMany(mappedBy = "review", cascade = CascadeType.ALL, orphanRemoval = true)
    private Set<ReviewTag> tags = new HashSet<>();

    @OneToMany(mappedBy = "review", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<ReviewFile> photos = new ArrayList<>();

    @Builder
    public Review(Shop shop, Student student, String content) {
        this.shop = shop;
        this.student = student;
        this.content = content;
        this.like = 0L;
        this.unlike = 0L;
        this.havePhotos = false;
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
        deletedReviewTag.getTag().minusTagNum(1L);
    }

    public void detachAllReviewTag() {
        this.tags.forEach(t -> t.getTag().minusTagNum(1L));
        this.tags.clear();
    }
}
