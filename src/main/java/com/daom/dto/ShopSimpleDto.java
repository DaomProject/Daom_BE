package com.daom.dto;

import lombok.Builder;
import lombok.Data;

import java.util.List;

@Data
public class ShopSimpleDto {
    private Long id;
    private String name;
    private String categoryName;
    private String jehueService;
    private String jehueDiscount;
    private String jehueCoupon;
    private String thumbnail;
    private List<String> tags;
    private Integer likeNum;
    private Integer reviewNum;
    private Integer zzimNum;
    private String description;
    private List<String> menuNames;

    @Builder
    public ShopSimpleDto(Long id, String name, String categoryName,
                         String thumbnail, List<String> tags, Integer likeNum, Integer reviewNum,
                         String jehueService, String jehueCoupon, String jehueDiscount,
                         Integer zzimNum, String description, List<String> menuNames) {
        this.id = id;
        this.name = name;
        this.categoryName = categoryName;
        this.thumbnail = thumbnail;
        this.tags = tags;
        this.jehueService = jehueService;
        this.jehueCoupon = jehueCoupon;
        this.jehueDiscount = jehueDiscount;
        this.likeNum = likeNum;
        this.reviewNum = reviewNum;
        this.zzimNum = zzimNum;
        this.description = description;
        this.menuNames = menuNames;
    }
}
