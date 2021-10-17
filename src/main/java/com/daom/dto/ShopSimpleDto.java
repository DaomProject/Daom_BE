package com.daom.dto;

import lombok.Builder;
import lombok.Data;

import java.util.List;

@Data
public class ShopSimpleDto {
    private Long id;
    private String name;
    private String categoryName;
    private String thumbnail;
    private List<String> tags;
    private Integer likeNum;
    private Integer reviewNum;
    private Integer zzimNum;
    private String description;
    private String jehueDesc;
    private List<String> menuNames;

    @Builder
    public ShopSimpleDto(Long id, String name, String categoryName,
                         String thumbnail, List<String> tags, Integer likeNum, Integer reviewNum,
                         Integer zzimNum, String description, String jehueDesc, List<String> menuNames) {
        this.id = id;
        this.name = name;
        this.categoryName = categoryName;
        this.thumbnail = thumbnail;
        this.tags = tags;
        this.likeNum = likeNum;
        this.reviewNum = reviewNum;
        this.zzimNum = zzimNum;
        this.description = description;
        this.jehueDesc = jehueDesc;
        this.menuNames = menuNames;
    }
}
