package com.daom.domain;

import lombok.Getter;
import lombok.NoArgsConstructor;

import javax.persistence.*;

@NoArgsConstructor
@Getter
@Entity
public class ReviewTag {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "review_tag_id")
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "review_id") // FK 생성
    private Review review;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "tag_id") // FK 생성
    private Tag tag;

    public ReviewTag(Review review, Tag tag) {
        this.review = review;
        this.tag = tag;

        // 리뷰태그 생성되었으므로 태그 개수 1 추가
        tag.plusTagNum(1L);
    }

}
