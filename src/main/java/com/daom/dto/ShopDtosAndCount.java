package com.daom.dto;

import lombok.Getter;

import java.util.ArrayList;
import java.util.List;

@Getter
public class ShopDtosAndCount {
    private List<ShopSimpleDto> shopSimpleDtos = new ArrayList<>();
    private int totalSize;

    public ShopDtosAndCount(List<ShopSimpleDto> shopSimpleDtos, int totalSize) {
        this.shopSimpleDtos = shopSimpleDtos;
        this.totalSize = totalSize;
    }
}
