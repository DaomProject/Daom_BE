package com.daom.dto;

import com.daom.domain.Member;
import com.daom.domain.Student;
import com.daom.domain.Univ;
import lombok.Data;

@Data
public class MyInfoStudentDto {
    private String username;
    private String nickname;
    private String univname;
    private String tel;
    private Long admissionYear;
    private Long point;
    private String thumbnail;
    private int level;
    private int reviewNum;
    private int likeNum;


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
        if(student.getThumbnail() != null){
            this.thumbnail = "http://localhost:8080/file?filename=" + student.getThumbnail().getSavedName();
        }else{
            this.thumbnail = null;
        }

        reviewNum = 0;
        likeNum = 0;
    }
}
