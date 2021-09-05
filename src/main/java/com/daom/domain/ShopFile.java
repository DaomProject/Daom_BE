package com.daom.domain;

import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import javax.persistence.*;

@Getter
@NoArgsConstructor
@Entity
public class ShopFile {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "shop_file_id")
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "shop_id") // FK 생성
    private Shop shop;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "file_id") // FK 생성
    private UploadFile file;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false, name = "desc")
    private FileDesc desc;

    @Builder
    public ShopFile(Shop shop, UploadFile file, FileDesc desc) {
        this.shop = shop;
        this.file = file;
        this.desc = desc;
    }

    public void connectShop(Shop shop){
        this.shop = shop;
    }
}
