package com.daom.domain;

import lombok.Getter;
import lombok.NoArgsConstructor;

import javax.persistence.*;

@Getter
@NoArgsConstructor
@Entity
public class Major {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "major_id")
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "univ_id") // FK 생성
    private Univ univ;

    @Column(nullable = false)
    private String name;
}
