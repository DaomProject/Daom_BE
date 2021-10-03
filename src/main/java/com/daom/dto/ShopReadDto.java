package com.daom.dto;

import com.fasterxml.jackson.annotation.JsonFormat;
import lombok.Builder;
import lombok.Data;

import java.time.LocalTime;
import java.util.List;

@Data
public class ShopReadDto {

    private Long id;

    private String categoryName;

    private String thumbnail;
    // 가게 이름
    private String name;

    // 가게 전화번호
    private String tel;

    // 제휴혜택 설명
    private String jehueDesc;

    // 가게 설명
    private String description;

    // 가게 주소 ( ShopReadDto 에서는 locDesc 하나만 존재 )
    private String locDesc;

    // 영업 요일
    private String workWeek;

    @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "HH:mm", timezone = "Asia/Seoul")
    private LocalTime startTime;

    @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "HH:mm", timezone = "Asia/Seoul")
    private LocalTime endTime;

    private List<MenuReadDto> menus;

    @Builder
    public ShopReadDto(Long id, String categoryName, String thumbnail, String name, String tel,
                       String jehueDesc, String description, String locDesc,
                       String workWeek, LocalTime startTime, LocalTime endTime, List<MenuReadDto> menus) {
        this.id = id;
        this.categoryName = categoryName;
        this.thumbnail = thumbnail;
        this.name = name;
        this.tel = tel;
        this.jehueDesc = jehueDesc;
        this.description = description;
        this.locDesc = locDesc;
        this.workWeek = workWeek;
        this.startTime = startTime;
        this.endTime = endTime;
        this.menus = menus;
    }
}
