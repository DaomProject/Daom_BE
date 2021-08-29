package com.daom.domain;

import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import javax.persistence.*;
import java.sql.Time;
import java.time.LocalDateTime;
import java.time.LocalTime;

@NoArgsConstructor
@Getter
@Entity
public class Shop extends BaseTimeEntity{

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "shop_id")
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "member_id")
    private Member member;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "category_id")
    private Category category;

    @Column(nullable = false)
    private String name;

    @Column(nullable = false)
    private String tel;

    @Column(nullable = false, name = "jehue_desc")
    private String jehueDesc;

    @Column(nullable = false, name="is_premium")
    private Boolean isPremium;

    @Column(nullable = false)
    private String description;

    @Column(nullable = false, name = "loc_desc")
    private String locDesc;

    @Column(nullable = false, name = "location_x")
    private Double locX;

    @Column(nullable = false, name = "location_y")
    private Double locY;

    @Column(nullable = false, name = "start_time")
    private LocalTime startTime;

    @Column(nullable = false, name = "end_time")
    private LocalTime endTime;

    @Column(nullable = false, name = "like_num")
    private Long like;

    @Column(nullable = false, name = "unlike_num")
    private Long unlike;

    //썸네일 관련 TODO


    @Builder
    public Shop(Member member, Category category, String name, String tel,
                String jehueDesc, Boolean isPremium, String description,
                String locDesc, Double locX, Double locY,
                LocalTime startTime, LocalTime endTime,
                Long like, Long unlike) {
        this.member = member;
        this.category = category;
        this.name = name;
        this.tel = tel;
        this.jehueDesc = jehueDesc;
        this.isPremium = isPremium;
        this.description = description;
        this.locDesc = locDesc;
        this.locX = locX;
        this.locY = locY;
        this.startTime = startTime;
        this.endTime = endTime;
        this.like = like;
        this.unlike = unlike;
    }
}
