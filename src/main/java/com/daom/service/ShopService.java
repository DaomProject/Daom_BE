package com.daom.service;

import com.daom.domain.*;
import com.daom.dto.MenuDto;
import com.daom.dto.ShopAndMenuFilesDto;
import com.daom.dto.ShopCreateDto;
import com.daom.exception.NoSuchCategoryException;
import com.daom.exception.NoSuchShopException;
import com.daom.exception.NoSuchShopFileException;
import com.daom.exception.NotAuthorityThisJobException;
import com.daom.repository.*;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.stream.Collectors;

@RequiredArgsConstructor
@Transactional(readOnly = true)
@Slf4j
@Service
public class ShopService {

    private final CategoryRepository categoryRepository;
    private final ShopRepository shopRepository;
    private final ShopFileRepository shopFileRepository;
    private final UploadFileRepository uploadFileRepository;
    private final FileStorage fileStorage;
    private final MenuRepository menuRepository;

    // 업체 등록 ( 처음 등록 )
    @Transactional
    public void createShop(Member member, ShopCreateDto
            shopCreateDto, ShopAndMenuFilesDto shopAndMenuFilesDto) {

        // locDesc로 주소 API 사용하여 locX, locY 찾기
//        List<double> shopXY = findShopXY(shopCreateDto);
        List<Double> shopXY = new ArrayList<>();
        // 테스트용
        shopXY.add(0.5);
        shopXY.add(0.3);
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

        if (shopAndMenuFilesDto.getThumbnail() != null) {

            UploadFile thumbnailFile = fileStorage.storeFile(shopAndMenuFilesDto.getThumbnail());

            ShopFile shopThumbnail = ShopFile.builder()
                    .shop(newShop)
                    .file(thumbnailFile)
                    .desc(FileDesc.THUMBNAIL).build();

            newShop.addShopFile(shopThumbnail);
        }
        // 메뉴 엔티티 생성

        // - 메뉴 객체 생성
        List<MenuDto> menus = shopCreateDto.getMenus();
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
                Menu menuHavingFile = menuList.get(havingFileIndex - 1);
                menuHavingFile.addThumbnail(menuFileList.get(index++));
            }
        }

        // 업체 - 메뉴 삽입
        menuList.forEach(newShop::addMenu);

        // 업체 Repository로 업체 저장
        shopRepository.save(newShop);

    }

    @Transactional
    public void deleteShop(Long loginMemberId, Long id) {

        Shop shop = shopRepository.findByIdWithMember(id).orElseThrow(NoSuchShopException::new);

        if (!Objects.equals(loginMemberId, shop.getMember().getId())) {
            throw new NotAuthorityThisJobException();
        }

        ShopFile shopFile = shopFileRepository.findByShop(shop).orElse(null);
        List<Menu> menus = menuRepository.findAllByShop(shop);

        List<String> fileSavedNames = new ArrayList<>();

        // 실제 데이터 삭제
        if (menus != null) {
            menus.forEach(menu -> {
                if (menu.getThumbnail() != null)
                    fileSavedNames.add(menu.getThumbnail().getSavedName());
            });
        }
        if (shopFile != null) {
            fileSavedNames.add(shopFile.getFile().getSavedName());
        }

        fileSavedNames.forEach(fileStorage::deleteFile);

        // DB 삭제
        shopRepository.delete(shop);

    }

}
