package com.daom.dto;

import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
public class MemberJoinDto {
    private String username;
    private String password;
    private String nickname;
    private String tel;
    private String mail;

    @Builder
    public MemberJoinDto(String username, String password, String nickname, String tel, String mail) {
        this.username = username;
        this.password = password;
        this.nickname = nickname;
        this.tel = tel;
        this.mail = mail;
    }
}
