package com.daom.dto;

import com.daom.domain.Member;
import com.daom.domain.Student;
import com.daom.domain.Univ;
import lombok.Builder;
import lombok.Data;

@Data
public class MyInfoStudentDto {
    private String username;
    private String nickname;
    private String univname;
    private String tel;
    private String mail;
    private Long admissionYear;
    private Long point;
    private String thumbnail;
    private int level;
    private int reviewNum;
    private int likeNum;

    @Builder
    public MyInfoStudentDto(String username, String nickname, String univname, String tel, String mail,
                            Long admissionYear, Long point, String thumbnail,
                            int level, int reviewNum, int likeNum) {
        this.username = username;
        this.nickname = nickname;
        this.univname = univname;
        this.tel = tel;
        this.mail = mail;
        this.admissionYear = admissionYear;
        this.point = point;
        this.thumbnail = thumbnail;
        this.level = level;
        //TODO reviewNum, likeNum은 아직 0으로만 설정. 이후에 추가시 수정 필요
        this.reviewNum = 0;
        this.likeNum = 0;
    }
}
