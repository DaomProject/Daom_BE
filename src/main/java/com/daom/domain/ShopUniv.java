package com.daom.domain;

import lombok.Getter;
import lombok.NoArgsConstructor;

import javax.persistence.*;

@NoArgsConstructor
@Getter
@Entity
public class ShopUniv {
    @Id @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "shop_univ_id")
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "shop_id") // FK 생성
    private Shop shop;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "univ_id") // FK 생성
    private Univ univ;
}
