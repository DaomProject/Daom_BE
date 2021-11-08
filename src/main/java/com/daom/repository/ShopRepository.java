package com.daom.repository;

import com.daom.domain.Member;
import com.daom.domain.Shop;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;
import java.util.Optional;

public interface ShopRepository extends JpaRepository<Shop, Long> {

    String HAVERSINE_FORMULA = "(6371 * acos(cos(radians(:lat)) * cos(radians(s.lat)) *" +
            " cos(radians(s.lon) - radians(:lon)) + sin(radians(:lat)) * sin(radians(s.lat))))";

    @Query("select s from Shop s where " + HAVERSINE_FORMULA + "< :distance" +
            " order by " + HAVERSINE_FORMULA)
    List<Shop> findPageByDistance(Pageable pageable, @Param("distance") double distance, @Param("lat") double nowLat, @Param("lon") double nowLon);

    @Query("select count(s) from Shop s where " + HAVERSINE_FORMULA + "< :distance" +
            " order by " + HAVERSINE_FORMULA)
    int countByDistance(@Param("distance") double distance, @Param("lat") double nowLat, @Param("lon") double nowLon);

    @Query("select s from Shop s" +
            " join fetch s.member smember" +
            " left outer join fetch s.shopFile sfile" +
            " left outer join fetch sfile.file file" +
            " join fetch s.menus smenus" +
            " left outer join fetch smenus.thumbnail mthumb" +
            " where s.id = :id")
    Optional<Shop> findByIdWithMemberAndFiles(@Param("id") Long id);

    @Query("select s from Shop s" +
            " join fetch s.category cat" +
            " left outer join fetch s.shopFile sfile" +
            " left outer join fetch sfile.file file" +
            " where s.member = :member")
    Optional<List<Shop>> findByMemberWithFiles(@Param("member") Member member);

    @Query("select s from Shop s" +
            " where " + HAVERSINE_FORMULA + "< :distance" +
            " order by s.like desc")
    List<Shop> findPageByLikeNum(Pageable pageable, @Param("distance") double distance, @Param("lat") double nowLat, @Param("lon") double nowLon);

    @Query(value = "select s from Shop s" +
            " left join s.reviews r" +
            " where " + HAVERSINE_FORMULA + "< :distance" +
            " group by s.id" +
            " order by count(r.shop) desc")
    List<Shop> findPageByReviewNum(Pageable pageable, @Param("distance") double distance, @Param("lat") double nowLat, @Param("lon") double nowLon);
}
