package com.daom.domain;

import com.daom.dto.MenuDto;
import com.daom.dto.MenuReadDto;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import javax.persistence.*;

@NoArgsConstructor
@Getter
@Entity
public class Menu {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "menu_id")
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "shop_id")
    private Shop shop;

    @OneToOne(fetch = FetchType.LAZY, cascade = CascadeType.ALL, orphanRemoval = true)
    @JoinColumn(name = "file_id")
    private UploadFile thumbnail;

    @Column(nullable = false)
    private String name;

    @Column(nullable = false, length = 500)
    private String description; // 메뉴 설명

    @Column(nullable = false)
    private Long price;

    @Column(nullable = false)
    private Boolean isRecommend;

    public void connectShop(Shop shop) {
        this.shop = shop;
    }

    @Builder
    public Menu(MenuDto menuDto) {
        this.name = menuDto.getName();
        this.price = menuDto.getPrice();
        this.isRecommend = menuDto.getIsRecommend();
        this.description = menuDto.getDescription();
        this.thumbnail = null;
    }

    @Builder
    public Menu(UploadFile thumbnail, String name, Long price, Boolean isRecommend, String description) {
        this.thumbnail = thumbnail;
        this.name = name;
        this.price = price;
        this.isRecommend = isRecommend;
        this.description = description;
    }

    public void addThumbnail(UploadFile thumbnail) {
        this.thumbnail = thumbnail;
    }

    public MenuReadDto toReadDto() {

        String thumbnailSavedName = "";
        if (thumbnail != null) {
            thumbnailSavedName = thumbnail.getSavedName();
        }

        return MenuReadDto.builder()
                .name(name)
                .price(price)
                .isRecommend(isRecommend)
                .thumbnail(thumbnailSavedName)
                .description(description)
                .build();
    }

}
