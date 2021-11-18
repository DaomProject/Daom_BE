package com.daom.domain;

import com.daom.dto.*;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import javax.persistence.*;
import java.time.LocalTime;
import java.util.*;
import java.util.stream.Collectors;

@NoArgsConstructor
@Getter
@Entity
public class Shop extends BaseTimeEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "shop_id")
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "member_id")
    private Member member;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "category_id")
    private Category category;

    @Column(nullable = false)
    private String name;

    @Column(nullable = false)
    private String tel;

    @Column(nullable = false, name = "jehue_service")
    private String jehueService;

    @Column(nullable = false, name = "jehue_discount")
    private String jehueDiscount;

    @Column(nullable = false, name = "jehue_coupon")
    private String jehueCoupon;

    @Column(nullable = false, name = "is_premium")
    private Boolean isPremium;

    @Column(nullable = false)
    private String description;

    // 가게 주소 ( API로 찾아볼 주소 )
    @Column(nullable = false, name = "loc_desc")
    private String locDesc;

    // 상세 주소 ( 추가적인 가게주소설명 O층, OOO동 등 )
    @Column(nullable = true, name = "loc_detail_desc")
    private String locDetailDesc;

    @Column(nullable = false, name = "latitude")
    private Double lat;

    @Column(nullable = false, name = "longitude")
    private Double lon;

    // 영업 요일 ( 월화수, 월화수목금, 수목금 이런식으로 String으로 저장 , 무휴면 null)
    @Column(nullable = true, name = "work_week")
    private String workWeek;

    @Column(nullable = false, name = "start_time")
    private LocalTime startTime;

    @Column(nullable = false, name = "end_time")
    private LocalTime endTime;

    @Column(nullable = false, name = "like_num")
    private int like;

    @Column(nullable = false, name = "zzim_num")
    private int zzimNum;

    @OneToMany(mappedBy = "shop", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<Menu> menus = new ArrayList<>();

    @OneToOne(mappedBy = "shop", cascade = CascadeType.ALL, orphanRemoval = true)
    private ShopFile shopFile;

    @OneToMany(mappedBy = "shop", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<Review> reviews = new ArrayList<>();

    @OneToMany(mappedBy = "shop", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<ShopTag> tags = new ArrayList<>();

    @OneToMany(mappedBy = "shop", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<StudentLikeShop> studentLikeShops = new ArrayList<>();

    @OneToMany(mappedBy = "shop", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<Zzim> zzims = new ArrayList<>();

    private static final int REVIEW_SIZE = 3;

    @Builder
    public Shop(Member member, Category category, String name, String tel,
                String description, String workWeek, String jehueCoupon, String jehueService, String jehueDiscount,
                String locDesc, String locDetailDesc, Double longitude, Double latitude,
                LocalTime startTime, LocalTime endTime) {
        this.member = member;
        this.category = category;
        this.name = name;
        this.tel = tel;
        this.jehueCoupon = jehueCoupon;
        this.jehueService = jehueService;
        this.jehueDiscount = jehueDiscount;
        this.isPremium = false;
        this.description = description;
        this.locDesc = locDesc;
        this.locDetailDesc = locDetailDesc;
        this.workWeek = workWeek;
        this.lon = longitude; // 좌표는 검색API 이용
        this.lat = latitude; // 검색 API 이용
        this.startTime = startTime;
        this.endTime = endTime;
        this.like = 0;
        this.zzimNum = 0;
    }

    public void addMenu(Menu menu) {
        menus.add(menu);
        menu.connectShop(this);
    }

    public void addShopFile(ShopFile shopFile) {
        this.shopFile = shopFile;
        shopFile.connectShop(this);
    }

    public void updateByDto(ShopCreateDto shopCreateDto, Category category) {
        // 태그, 파일, thumbnail을 제외한 모든 수정을 담당
        this.category = category;

        this.name = shopCreateDto.getName();
        this.tel = shopCreateDto.getTel();
        this.jehueService = shopCreateDto.getJehueService();
        this.jehueDiscount = shopCreateDto.getJehueDiscount();
        this.jehueCoupon = shopCreateDto.getJehueCoupon();
        this.description = shopCreateDto.getDescription();
        this.locDesc = shopCreateDto.getLocDesc();
        this.locDetailDesc = shopCreateDto.getLocDetailDesc();
        this.workWeek = shopCreateDto.getWorkWeek();
        this.startTime = shopCreateDto.getStartTime();
        this.endTime = shopCreateDto.getEndTime();
    }

    public void changeXY(Double lat, Double lon) {
        this.lat = lat;
        this.lon = lon;
    }

    public void detachShopTag(ShopTag deletedShopTag) {
        this.tags.remove(deletedShopTag);
        deletedShopTag.getTag().minusTagNum(1);
    }

    public void detachAllShopTag() {
        this.tags.forEach(t -> t.getTag().minusTagNum(1));
        this.tags.clear();
    }

    public void plusZzimNum() {
        this.zzimNum += 1;
    }

    public void minusZzimNum() {
        this.zzimNum -= 1;
    }

    public void plusLikeNum() {
        this.like += 1;
    }

    public void minusLikeNum() {
        this.like -= 1;
    }

    public ShopReadDto toShopReadDto() {
//         Shop thumb 주소 얻기
        String thumbnailSavedName = "";
        if (this.shopFile != null) {
            thumbnailSavedName = this.shopFile.getFile().getSavedName();
        }

        // 태그 얻기
        List<String> tagNames = new ArrayList<>();
        tags.forEach(t -> tagNames.add(t.getTag().getName()));

        // List<Menu> -> List<MenuReadDto> + thumb 주소얻기까지 해야함
        List<MenuReadDto> menuDtoList = menus.stream().map(Menu::toReadDto).collect(Collectors.toList());

        List<ReviewReadDto> reviewDtoList = reviews.stream().map(Review::toReadDto).collect(Collectors.toList());
        int totalReviewNum = reviewDtoList.size();

        Collections.reverse(reviewDtoList); // 최신순으로 조회를 원하기 때문에 리스트를 뒤집음

        List<ReviewReadDto> photoReviewDtoList = reviewDtoList.stream().filter(reviewDto -> !reviewDto.getPhotos().isEmpty()).collect(Collectors.toList());
        reviewDtoList.removeAll(photoReviewDtoList); // reviewDtoList에서 PhotoReivew를 제외
        if (reviewDtoList.size() > REVIEW_SIZE) {
            reviewDtoList = reviewDtoList.subList(0, REVIEW_SIZE);
        }

        if (photoReviewDtoList.size() > REVIEW_SIZE) {
            photoReviewDtoList = photoReviewDtoList.subList(0, REVIEW_SIZE);
        }

        return ShopReadDto.builder()
                .id(id)
                .categoryName(category.getName())
                .thumbnail(thumbnailSavedName)
                .name(name)
                .tel(tel)
                .jehueService(jehueService)
                .jehueCoupon(jehueCoupon)
                .jehueDiscount(jehueDiscount)
                .description(description)
                .locDesc(locDesc + " " + locDetailDesc)
                .workWeek(workWeek)
                .startTime(startTime)
                .endTime(endTime)
                .menus(menuDtoList)
                .textReviews(reviewDtoList)
                .photoReviews(photoReviewDtoList)
                .tags(tagNames)
                .totalReviewNum(totalReviewNum)
                .totalZzimNum(zzimNum)
                .likeNum(like)
                .build();
    }

    public ShopSimpleDto toShopSimpleDto() {
        // Shop thumb 주소 얻기
        String thumbnailSavedName = "";
        if (this.shopFile != null) {
            thumbnailSavedName = this.shopFile.getFile().getSavedName();
        }

        // 태그 얻기
        List<String> tagNames = new ArrayList<>();
        tags.forEach(t -> tagNames.add(t.getTag().getName()));

        // List<Menu> -> List<MenuReadDto> + thumb 주소얻기까지 해야함
        List<String> menuNames = menus.stream().filter(Menu::getIsRecommend).map(Menu::getName).collect(Collectors.toList());

        int totalReviewNum = reviews.size();

        return ShopSimpleDto.builder()
                .id(id)
                .thumbnail(thumbnailSavedName)
                .categoryName(category.getName())
                .description(description)
                .jehueDiscount(jehueDiscount)
                .jehueService(jehueService)
                .jehueCoupon(jehueCoupon)
                .name(name)
                .tags(tagNames)
                .likeNum(like)
                .zzimNum(zzimNum)
                .reviewNum(totalReviewNum)
                .menuNames(menuNames)
                .build();
    }

    public void detachShopFile() {
        this.shopFile = null;
    }
}
