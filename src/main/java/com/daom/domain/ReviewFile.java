package com.daom.domain;

import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import javax.persistence.*;

@Getter
@NoArgsConstructor
@Entity
public class ReviewFile {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "review_file_id")
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "review_id") // FK 생성
    private Review review;

    @ManyToOne(fetch = FetchType.LAZY, cascade = CascadeType.ALL)
    @JoinColumn(name = "file_id") // FK 생성
    private UploadFile file;

    @Builder
    public ReviewFile(Review review, UploadFile file) {
        this.review = review;
        this.file = file;
    }

    public void connectReview(Review review){
        this.review = review;
    }
}
