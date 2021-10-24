package com.daom.repository;

import com.daom.domain.Zzim;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface ZzimRepository extends JpaRepository<Zzim, Long> {
    Optional<Zzim> findByStudentIdAndShopId(Long studentId, Long shopId);

    List<Zzim> findByShopId(Long id);
}
