package com.daom.repository;

import com.daom.domain.Shop;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.Optional;

public interface ShopRepository extends JpaRepository<Shop, Long> {
    @Query("select s from Shop s" +
            " join fetch s.member smember" +
            " join fetch s.shopFile sfile" +
            " join fetch sfile.file file" +
            " join fetch s.menus smenus" +
            " left outer join fetch smenus.thumbnail mthumb" +
            " where s.id = :id")
    Optional<Shop> findByIdWithMemberAndFiles(@Param("id") Long id);
}
