package com.daom.dto;

import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
public class MenuReadDto {
    private String name;
    private Long price;
    private String description;
    private Boolean isRecommend;
    private String thumbnail;

    @Builder
    public MenuReadDto(String name, Long price, Boolean isRecommend, String thumbnail, String description) {
        this.name = name;
        this.price = price;
        this.isRecommend = isRecommend;
        this.thumbnail = thumbnail;
        this.description = description;
    }
}
