package com.daom.domain;

import lombok.Getter;
import lombok.NoArgsConstructor;

import javax.persistence.*;
@Getter
@NoArgsConstructor
@Entity
public class StudentLikeUnlikeReview extends CreateTimeEntity {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "student_like_unlike_review_id")
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "student_id")
    private Student student;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "review_id")
    private Review review;

    // true = like, false = unlike
    @Column(name = "is_like")
    private boolean isLike;

    public StudentLikeUnlikeReview(Student student, Review review, boolean isLike) {
        this.student = student;
        this.review = review;
        this.isLike = isLike;
    }

    public void likeTogle(){
        isLike = !isLike;
    }
}