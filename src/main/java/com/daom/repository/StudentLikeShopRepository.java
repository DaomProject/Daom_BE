package com.daom.repository;

import com.daom.domain.StudentLikeShop;
import com.daom.domain.Zzim;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface StudentLikeShopRepository extends JpaRepository<StudentLikeShop, Long> {
    Optional<StudentLikeShop> findByStudentIdAndShopId(Long studentId, Long shopId);
    List<StudentLikeShop> findByShopId(Long id);
}
