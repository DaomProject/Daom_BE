package com.daom.repository;

import com.daom.domain.Review;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.util.Optional;

public interface ReviewRepository extends JpaRepository<Review, Long> {
    @Query("select r from Review r" +
            " join fetch r.student s" +
            " left join fetch r.tags rt" +
            " left join fetch rt.tag t" +
            " left join fetch r.photos p" +
            " left join fetch p.file pf")
    Optional<Review> findByIdWithFilesAndTags(Long reviewId);
}
