package com.daom.repository;

import com.daom.domain.Shop;
import com.daom.domain.ShopFile;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface ShopFileRepository extends JpaRepository<ShopFile, Long> {
    Optional<ShopFile> findByShop(Shop shop);
}
