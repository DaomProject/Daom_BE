package com.daom.repository;

import com.daom.domain.Shop;
import com.daom.domain.Student;
import com.daom.domain.StudentVisitShop;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface StudentVisitShopRepository extends JpaRepository<StudentVisitShop, Long> {
    List<StudentVisitShop> findAllByStudentAndShop(Student student, Shop shop);
}
