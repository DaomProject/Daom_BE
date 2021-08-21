package com.daom.domain;

import lombok.Getter;
import lombok.NoArgsConstructor;

import javax.persistence.*;
import java.time.LocalTime;

@NoArgsConstructor
@Getter
@Entity
public class Shop {

    @Id @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "shop_id")
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "member_id")
    private Member member;

    @Column(nullable = false)
    private String name;

    @Column(nullable = false)
    private String description;

    @Column(nullable = false, name ="loc_desc")
    private String locDesc;

    @Column(nullable = false, name ="location_x")
    private Double locX;

    @Column(nullable = false, name ="location_y")
    private Double locY;

    @Column(nullable = false, name ="is_premium")
    private Boolean isPremium;

    @Column(nullable = false, name = "start_time")
    private LocalTime startTime;

    @Column(nullable = false, name = "end_time")
    private LocalTime endTime;

    @Column(nullable = false, name ="like_num")
    private Long like;
    @Column(nullable = false, name ="unlike_num")
    private Long unlike;

}
