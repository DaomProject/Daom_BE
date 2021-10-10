package com.daom.repository;

import com.daom.domain.Review;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;
import java.util.Optional;

public interface ReviewRepository extends JpaRepository<Review, Long> {
    @Query("select r from Review r" +
            " join fetch r.student s" +
            " left join fetch r.tags rt" +
            " left join fetch rt.tag t" +
            " left join fetch r.photos p" +
            " left join fetch p.file pf" +
            " where r.id = :id")
    Optional<Review> findByIdWithFilesAndTags(@Param("id") Long reviewId);

    @Query("select r from Review r" +
            " join fetch r.student s" +
            " where r.havePhotos = false" +
            " order by r.id desc")
    List<Review> findByPageWithNotPhotos(Pageable pageable);

    @Query("select r from Review r" +
            " join fetch r.student s" +
            " where r.havePhotos = true" +
            " order by r.id desc")
    List<Review> findByPageWithPhotos(Pageable pageable);

    long countByHavePhotos(boolean havePhotos);
}
