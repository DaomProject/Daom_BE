package com.daom.dto;

import com.daom.domain.Member;
import com.daom.domain.Student;
import com.daom.domain.Univ;
import lombok.Builder;
import lombok.Data;
import org.springframework.transaction.annotation.Transactional;

@Data
public class MyInfoStudentDto {
    private String username;
    private String nickname;
    private String univname;
    private String tel;
    private Long admissionYear;
    private Long point;
    private int level;
    private int reviewNum;
    private int likeNum;
    //TODO 썸네일

//    @Builder
//    public MyInfoStudentDto(String username, String nickname, String univname, String tel, Long admissionYear, Long point, int level) {
//        this.username = username;
//        this.nickname = nickname;
//        this.univname = univname;
//        this.tel = tel;
//        this.admissionYear = admissionYear;
//        this.point = point;
//        this.level = level;
//
//        // TODO
//        this.reviewNum = 0;
//        this.likeNum = 0;
//    }

    public MyInfoStudentDto(Member member){
        Student student = member.getStudent();
        this.username = member.getUsername();
        this.nickname = member.getNickname();
        this.tel = member.getTel();

        Univ univ = student.getUniv();
        this.univname = univ.getName();
        this.admissionYear = student.getAdmissionYear();
        this.point = student.getPoint();
        this.level = student.getLevel();

        // TODO
        reviewNum = 0;
        likeNum = 0;
    }
}
