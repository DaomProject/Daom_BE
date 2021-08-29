package com.daom.domain;

import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import javax.persistence.*;

@Getter
@NoArgsConstructor
@Entity
public class Student extends BaseTimeEntity{

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "student_id")
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "member_id") // FK 생성
    private Member member;

    @Column(nullable = false)
    private String nickname;

    // 재학중인 대학교
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "univ_id")
    private Univ univ;

    // 입학년도
    @Column(name = "admission_year", nullable = false)
    private Long admissionYear;

    // 신뢰도
    @Column(nullable = false)
    private Long point;

    // 레벨
    @Column(nullable = false)
    private Long grade;

    // 프로필 이미지 관련 TODO

    @Builder
    public Student(Member member, String nickname, Univ univ, Long admissionYear) {
        this.member = member;
        this.nickname = nickname;
        this.univ = univ;
        this.admissionYear = admissionYear;
        this.point = 0L;
        this.grade = 0L;
    }
}
