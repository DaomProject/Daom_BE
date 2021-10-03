package com.daom.dto;

import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@Data
@NoArgsConstructor
public class ReviewCreateDto {

    private String content;
    private List<String> tags;
}
