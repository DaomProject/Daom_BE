package com.daom.repository;

import com.daom.domain.Shop;
import com.daom.domain.Student;
import com.daom.domain.StudentVisitShop;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface StudentVisitShopRepository extends JpaRepository<StudentVisitShop, Long> {
    Optional<StudentVisitShop> findByStudentAndShop(Student student, Shop shop);
}
