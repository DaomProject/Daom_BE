package com.daom.dto;

import lombok.Getter;

import java.util.List;

@Getter
public class ReviewDtosAndCount {
    private List<ReviewReadDto> reviewDtos;
    private int totalSize;

    public ReviewDtosAndCount(List<ReviewReadDto> reviewDtos, int totalSize) {
        this.reviewDtos = reviewDtos;
        this.totalSize = totalSize;
    }
}
