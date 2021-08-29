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
    private String univname;
    private String tel;
    private Long admissionYear;

    @Builder
    public StudentJoinDto(String username, String password, String nickname, String univName, Long admissionYear, String tel) {
        this.username = username;
        this.password = password;
        this.nickname = nickname;
        this.univname = univName;
        this.admissionYear = admissionYear;
        this.tel = tel;
    }
}
