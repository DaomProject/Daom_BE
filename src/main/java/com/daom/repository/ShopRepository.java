package com.daom.repository;

import com.daom.domain.Shop;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.util.Optional;

public interface ShopRepository extends JpaRepository<Shop, Long> {
    @Query("select s from Shop s join fetch s.member")
    Optional<Shop> findByIdWithMember(Long id);
}
