package com.daom.service;

import com.daom.domain.*;
import com.daom.dto.MenuDto;
import com.daom.dto.ShopAndMenuFilesDto;
import com.daom.dto.ShopCreateDto;
import com.daom.exception.NoSuchCategoryException;
import com.daom.repository.CategoryRepository;
import com.daom.repository.MenuRepository;
import com.daom.repository.ShopRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

@RequiredArgsConstructor
@Service
public class ShopService {

    private final CategoryRepository categoryRepository;
    private final MenuRepository menuRepository;
    private final ShopRepository shopRepository;

    // 업체 등록 ( 처음 등록 )
    public void createShop(Member member, ShopCreateDto
            shopCreateDto, ShopAndMenuFilesDto shopAndMenuFilesDto) {

        // locDesc로 주소 API 사용하여 locX, locY 찾기
//        List<double> shopXY = findShopXY(shopCreateDto);
        List<Double> shopXY = new ArrayList<>();
        double locX = shopXY.get(0);
        double locY = shopXY.get(1);

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
                .workWeek(shopCreateDto.getWorkWeek())
                .startTime(shopCreateDto.getStartTime())
                .endTime(shopCreateDto.getEndTime())
                .locX(locX)
                .locY(locY)
                .build();

        // 메뉴 엔티티 생성
        // - 메뉴 파일 객체 생성
        List<MultipartFile> menuMultipartFiles = shopAndMenuFilesDto.getMenuFiles();
        List<Integer> haveFileMenuIndex = shopAndMenuFilesDto.getHaveFileMenuIndex();

        // - 파일 저장
        List<File> menuFileList = FileStorage.addFiles(menuMultipartFiles);
        int index = 0;

        // - 메뉴 객체 생성 및 메뉴에 파일 삽입
        List<MenuDto> menus = shopCreateDto.getMenus();
        List<Menu> menuList = menus.stream().map(menu -> new Menu(menu)).collect(Collectors.toList());

        for (int fileIndex : haveFileMenuIndex) {
            menuList.get(fileIndex - 1).addThumbnail(menuFileList.get(index++));
        }

        // 업체 - 메뉴 삽입
        menuList.forEach((menu)->{
            newShop.addMenu(menu);
        });

        // 업체 Repository로 업체 저장
        shopRepository.save(newShop);

    }

}
