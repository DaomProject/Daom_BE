package com.daom.domain;

import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import javax.persistence.*;

@Getter
@NoArgsConstructor
@Entity
public class Student {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "student_id")
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "member_id") // FK 생성
    private Member member;

//    @ManyToOne(fetch = FetchType.LAZY)
//    @JoinColumn(name = "major_id")
//    private Major major;

    @Column(nullable = false)
    private String nickname;

    // 재학중인 대학이름
    @Column(name = "univ_name",nullable = false)
    private String univName;

    // 입학년도
    @Column(name = "admission_year", nullable = false)
    private Long admissionYear;

    // 신뢰도
    @Column(nullable = false)
    private Long point;

    // 등급 칭호
    @Column(nullable = false)
    private String grade;

    @Builder
    public Student(Member member, String nickname, String univName, Long admissionYear) {
        this.member = member;
        this.nickname = nickname;
        this.univName = univName;
        this.admissionYear = admissionYear;
        this.point = 0L;
        this.grade = "0레벨";
    }
}
