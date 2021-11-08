package com.daom.repository;

import com.daom.domain.Review;
import com.daom.domain.Student;
import com.daom.domain.StudentLikeShop;
import com.daom.domain.StudentLikeUnlikeReview;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface StudentLikeUnlikeReviewRepository extends JpaRepository<StudentLikeUnlikeReview, Long> {
    Optional<StudentLikeUnlikeReview> findByStudentAndReview(Student student, Review review);
}
