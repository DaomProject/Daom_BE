package com.daom.dto;

import com.fasterxml.jackson.annotation.JsonFormat;
import lombok.Builder;
import lombok.Data;

import java.time.LocalTime;
import java.util.List;

@Data
public class ShopCreateDto {
    private String categoryName;

    // 가게 이름
    private String name;

    // 가게 전화번호
    private String tel;

    // 제휴혜택 설명
    private String jehueDesc;

    // 가게 설명
    private String description;

    // 가게 주소
    private String locDesc;

    // 상세 주소설명
    private String locDetailDesc;

    // 영업 요일
    private String workWeek;

    @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "HH:mm", timezone = "Asia/Seoul")
    private LocalTime startTime;

    @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "HH:mm", timezone = "Asia/Seoul")
    private LocalTime endTime;

    private List<MenuDto> menus;

    @Builder
    public ShopCreateDto(String categoryName, String name, String tel,
                         String jehueDesc, String description, String locDesc, String locDetailDesc,
                         String workWeek, LocalTime startTime, LocalTime endTime, List<MenuDto> menus) {
        this.categoryName = categoryName;
        this.name = name;
        this.tel = tel;
        this.jehueDesc = jehueDesc;
        this.description = description;
        this.locDesc = locDesc;
        this.locDetailDesc = locDetailDesc;
        this.workWeek = workWeek;
        this.startTime = startTime;
        this.endTime = endTime;
        this.menus = menus;
    }
}
