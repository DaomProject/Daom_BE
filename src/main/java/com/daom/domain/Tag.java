package com.daom.domain;

import lombok.Getter;
import lombok.NoArgsConstructor;

import javax.persistence.*;

@Getter
@NoArgsConstructor
@Entity
public class Tag {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "tag_id")
    private Long id;

    @Column(nullable = false)
    private String name;

    @Column(nullable = false)
    private Long num;

    public Tag(String name) {
        this.name = name;
        this.num = 0L;
    }

    public void plusTagNum(Long n){
        this.num += n;
    }

    public void minusTagNum(Long n){
        this.num -= n;
        if(this.num < 0){
            this.num = 0L;
        }
    }
}
