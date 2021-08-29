package com.daom.domain;

import lombok.Getter;
import lombok.NoArgsConstructor;

import javax.persistence.*;

@Getter
@NoArgsConstructor
@Entity
public class Univ {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "univ_id")
    private Long id;

    @Column(nullable = false)
    private String name;

    public Univ(String name) {
        this.name = name;
    }
}
