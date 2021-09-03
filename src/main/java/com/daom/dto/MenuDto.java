package com.daom.dto;

import lombok.Data;

@Data
public class MenuDto {
    private String name;
    private Long price;
    private Boolean isRecommend;
}
