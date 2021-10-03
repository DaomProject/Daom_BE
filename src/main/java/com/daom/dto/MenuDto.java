package com.daom.dto;

import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
public class MenuDto {
    private String name;
    private Long price;
    private Boolean isRecommend;
}
