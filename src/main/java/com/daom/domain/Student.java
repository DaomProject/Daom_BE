package com.daom.domain;

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

    @Column(name = "student_num", nullable = false)
    private Long studentNum;

    @Column(nullable = false)
    private Long point;

    @Column(nullable = false)
    private String grade;
}
