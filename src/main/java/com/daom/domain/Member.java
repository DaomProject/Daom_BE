package com.daom.domain;

import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import javax.persistence.*;

@Entity
@Getter
@NoArgsConstructor
// ORM
public class Member extends BaseTimeEntity{
    @Id @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "member_id")
    private Long id;

    @Column(nullable = false)
    private String username;

    @Column(nullable = false)
    private String nickname;

    @Column(nullable = false)
    private String tel;

    @Column(nullable = false)
    private String password;

    @Enumerated(EnumType.STRING)
    private Role role;

    @OneToOne(mappedBy = "member", cascade = CascadeType.ALL, orphanRemoval = true, fetch = FetchType.LAZY)
    private Student student;

    @Builder
    public Member(String username, String password, Role role, String nickname, String tel){
        this.username = username;
        this.nickname = nickname;
        this.tel = tel;
        this.password = password;
        this.role = role;
    }

    public void connectStudent(Student student) {
        this.student = student;
    }
    public void changePw(String pw){ this.password = pw;}


}
