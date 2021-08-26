package com.daom.dto;

import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
public class StudentJoinDto {
    private String username;
    private String password;
    private String nickname;
    private String univName;
    private Long admissionYear;

    @Builder
    public StudentJoinDto(String username, String password, String nickname, String univName, Long admissionYear) {
        this.username = username;
        this.password = password;
        this.nickname = nickname;
        this.univName = univName;
        this.admissionYear = admissionYear;
    }
}
