package com.daom.service;

import com.daom.domain.*;
import com.daom.dto.*;
import com.daom.exception.*;
import com.daom.repository.*;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.List;
import java.util.Objects;
import java.util.Set;
import java.util.stream.Collectors;

@RequiredArgsConstructor
@Transactional(readOnly = true)
@Slf4j
@Service
public class ShopService {

    private final CategoryRepository categoryRepository;
    private final ShopRepository shopRepository;
    private final FileStorage fileStorage;
    private final NaverMapApi naverMapApi;
    private final ReviewService reviewService;

    @Value("${file.url}")
    private String fileUrl;

    // 업체 등록 ( 처음 등록 )
    @Transactional
    public void createShop(Member member, ShopCreateDto
            shopCreateDto, ShopAndMenuFilesDto shopAndMenuFilesDto) {

        if (member.getRole() != Role.SHOP) {
            throw new NotAuthorityThisJobException();
        }

        // locDesc로 주소 API 사용하여 locX, locY 찾기
        double[] shopXY = findShopXY(shopCreateDto.getLocDesc());
        double locX = shopXY[0];
        double locY = shopXY[1];

        // 카테고리 찾기
        Category category = categoryRepository.findByName(shopCreateDto.getCategoryName())
                .orElseThrow(NoSuchCategoryException::new);


        // 업체 엔티티 생성
        Shop newShop = Shop.builder()
                .member(member)
                .category(category)
                .name(shopCreateDto.getName())
                .tel(shopCreateDto.getTel())
                .jehueDesc(shopCreateDto.getJehueDesc())
                .description(shopCreateDto.getDescription())
                .locDesc(shopCreateDto.getLocDesc())
                .locDetailDesc(shopCreateDto.getLocDetailDesc())
                .workWeek(shopCreateDto.getWorkWeek())
                .startTime(shopCreateDto.getStartTime())
                .endTime(shopCreateDto.getEndTime())
                .locX(locX)
                .locY(locY)
                .build();

        if (shopAndMenuFilesDto.getThumbnail() != null) {

            UploadFile thumbnailFile = fileStorage.storeFile(shopAndMenuFilesDto.getThumbnail());

            ShopFile shopThumbnail = ShopFile.builder()
                    .shop(newShop)
                    .file(thumbnailFile)
                    .desc(FileDesc.THUMBNAIL).build();

            newShop.addShopFile(shopThumbnail);
        }
        // 메뉴 엔티티 생성

        List<MenuDto> menusDto = shopCreateDto.getMenus();
        List<Menu> menuList = makeMenusWithFile(menusDto, shopAndMenuFilesDto);

        // 업체 - 메뉴 삽입
        menuList.forEach(newShop::addMenu);

        // 업체 Repository로 업체 저장
        shopRepository.save(newShop);

    }

    private List<Menu> makeMenusWithFile(List<MenuDto> menus, ShopAndMenuFilesDto shopAndMenuFilesDto) {
        // - 메뉴 객체 생성
        List<Menu> menuList = menus.stream().map(Menu::new).collect(Collectors.toList());

        // - 메뉴 파일 객체 생성
        List<MultipartFile> menuMultipartFiles = shopAndMenuFilesDto.getMenuFiles();
        List<Integer> menuHavingFileIndexes = shopAndMenuFilesDto.getMenuHavingFileIndexes();

        // 파일이 존재할 때
        if (menuMultipartFiles != null && !menuMultipartFiles.isEmpty()) {

            // - 파일 저장
            List<UploadFile> menuFileList = fileStorage.storeFiles(menuMultipartFiles);
            int index = 0;

            for (int havingFileIndex : menuHavingFileIndexes) {
                try {
                    Menu menuHavingFile = menuList.get(havingFileIndex - 1);
                    menuHavingFile.addThumbnail(menuFileList.get(index++));
                } catch (IndexOutOfBoundsException e) {
                    throw new MenuIndexAndFileNotMatchException();
                }
            }
        }
        return menuList;
    }

    @Transactional
    public void deleteShop(Long loginMemberId, Long id) {

        Shop shop = shopRepository.findByIdWithMemberAndFiles(id).orElseThrow(NoSuchShopException::new);

        if (!Objects.equals(loginMemberId, shop.getMember().getId())) {
            throw new NotAuthorityThisJobException();
        }

        ShopFile shopFile = shop.getShopFile();
        List<Menu> menus = shop.getMenus();

        // 실제 데이터 삭제
        menuFilesDelete(menus);
        shopFileDelete(shopFile);

        List<Review> reviews = shop.getReviews();
        reviews.forEach(reviewService::deleteReview);

        // DB 삭제
        shopRepository.delete(shop);

    }

    // 파일 전체 수정
    @Transactional
    public void updateShop(Long loginMemberId, Long shopId, ShopCreateDto shopEditDto, ShopAndMenuFilesDto shopAndMenuFilesDto) {

        // 수정자와 작성자가 동일한지 확인
        Shop shop = shopRepository.findByIdWithMemberAndFiles(shopId).orElseThrow(NoSuchShopException::new);

        if (!Objects.equals(loginMemberId, shop.getMember().getId())) {
            throw new NotAuthorityThisJobException();
        }

        // locDesc로 주소 API 사용하여 locX, locY 찾기
        double[] shopXY = findShopXY(shopEditDto.getLocDesc());
        double locX = shopXY[0];
        double locY = shopXY[1];

        // 카테고리 찾기
        Category category = categoryRepository.findByName(shopEditDto.getCategoryName())
                .orElseThrow(NoSuchCategoryException::new);

        // 상점 정보 수정
        shop.updateByDto(shopEditDto, category);
        shop.changeXY(locX, locY);

        List<Menu> menus = shop.getMenus();
        ShopFile shopFile = shop.getShopFile();

        // 등록된 실제 파일들 다 제거
        menuFilesDelete(menus);
        shopFileDelete(shopFile);

        menus.clear();

        List<MenuDto> menusDto = shopEditDto.getMenus();
        List<Menu> menuList = makeMenusWithFile(menusDto, shopAndMenuFilesDto);

        // 메뉴 삽입
        menuList.forEach(shop::addMenu);

        // 썸네일 삽입
        if (shopAndMenuFilesDto.getThumbnail() != null) {

            UploadFile thumbnailFile = fileStorage.storeFile(shopAndMenuFilesDto.getThumbnail());

            ShopFile shopThumbnail = ShopFile.builder()
                    .shop(shop)
                    .file(thumbnailFile)
                    .desc(FileDesc.THUMBNAIL).build();

            shop.addShopFile(shopThumbnail);
        }

    }

    private void shopFileDelete(ShopFile shopFile) {
        if (shopFile != null) {
            fileStorage.deleteFile(shopFile.getFile().getSavedName());
        }
    }

    private void menuFilesDelete(List<Menu> menus) {
        if (!menus.isEmpty()) {
            List<UploadFile> uploadFiles = menus.stream()
                    .map(Menu::getThumbnail)
                    .filter(Objects::nonNull)
                    .collect(Collectors.toList());
            uploadFiles.forEach(uploadFile -> fileStorage.deleteFile(uploadFile.getSavedName()));
        }
    }

    private double[] findShopXY(String locDesc) {
        double[] result;
        try {
            result = naverMapApi.findShopXYApi(locDesc);
        } catch (IOException e) {
            throw new NaveMapApiException(e);
        }

        // 좌표를 찾지 못했을 시
        if (result[0] == 0) {
            throw new FindShopXYException();
        }

        return result;
    }

    public List<ShopReadDto> readMyShop(Member member) {
        List<Shop> shops = shopRepository.findByMemberWithFiles(member).orElseThrow(NoSuchShopException::new);
        shops.forEach(shop -> shop.getReviews()); // 강제 초기화를 위함

        return shops.stream().map(shop -> shop.toShopReadDto(fileUrl)).collect(Collectors.toList());
    }
}
