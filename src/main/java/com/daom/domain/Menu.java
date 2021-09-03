package com.daom.domain;

import com.daom.dto.MenuDto;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import javax.persistence.*;

@NoArgsConstructor
@Getter
@Entity
public class Menu {
    @Id @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "menu_id")
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "shop_id")
    private Shop shop;

    @OneToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "file_id")
    private File thumbnail;

    @Column(nullable = false)
    private String name;

    @Column(nullable = false)
    private Long price;

    @Column(nullable = false)
    private Boolean isRecommend;

    public void connectShop(Shop shop){
        this.shop = shop;
    }
    //썸네일 관련 TODO

    @Builder
    public Menu(MenuDto menuDto){
        this.name = menuDto.getName();
        this.price = menuDto.getPrice();
        this.isRecommend = menuDto.getIsRecommend();
        this.thumbnail = null;
    }

    @Builder
    public Menu(File thumbnail, String name, Long price, Boolean isRecommend) {
        this.thumbnail = thumbnail;
        this.name = name;
        this.price = price;
        this.isRecommend = isRecommend;
    }

    public void addThumbnail(File thumbnail){
        this.thumbnail = thumbnail;
    }
}
