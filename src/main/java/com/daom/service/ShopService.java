package com.daom.service;

import com.daom.domain.*;
import com.daom.dto.*;
import com.daom.exception.*;
import com.daom.repository.*;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.*;
import java.util.stream.Collectors;

@RequiredArgsConstructor
@Transactional(readOnly = true)
@Slf4j
@Service
public class ShopService {

    private final CategoryRepository categoryRepository;
    private final ShopRepository shopRepository;
    private final TagRepository tagRepository;
    private final FileCloudStorage fileCloudStorage;
    private final NaverMapApi naverMapApi;
    private final ReviewService reviewService;
    private final ZzimRepository zzimRepository;

    @Value("${file.url}")
    private String fileUrl;

    // 업체 등록 ( 처음 등록 )
    @Transactional
    public void createShop(Member member, ShopCreateDto shopCreateDto,
                           ShopAndMenuFilesDto shopAndMenuFilesDto) {

        if (member.getRole() != Role.SHOP) {
            throw new NotAuthorityThisJobException();
        }

        // locDesc로 주소 API 사용하여 locX, locY 찾기
        double[] shopXY = findShopXY(shopCreateDto.getLocDesc());
        // result[0] 이 경도(longitude) result[1]이 위도(latitude)
        double lon = shopXY[0];
        double lat = shopXY[1];

        // 카테고리 찾기
        Category category = categoryRepository.findByName(shopCreateDto.getCategoryName())
                .orElseThrow(NoSuchCategoryException::new);

        // 업체 엔티티 생성
        Shop newShop = Shop.builder()
                .member(member)
                .category(category)
                .name(shopCreateDto.getName())
                .tel(shopCreateDto.getTel())
                .jehueService(shopCreateDto.getJehueService())
                .jehueCoupon(shopCreateDto.getJehueCoupon())
                .jehueDiscount(shopCreateDto.getJehueDiscount())
                .description(shopCreateDto.getDescription())
                .locDesc(shopCreateDto.getLocDesc())
                .locDetailDesc(shopCreateDto.getLocDetailDesc())
                .workWeek(shopCreateDto.getWorkWeek())
                .startTime(shopCreateDto.getStartTime())
                .endTime(shopCreateDto.getEndTime())
                .latitude(lat)
                .longitude(lon)
                .build();

        // 태그 붙이기 ( 중복 제거 )
        List<String> tagNames = shopCreateDto.getTags();
        if (tagNames != null && !tagNames.isEmpty()) {
            Set<String> newTagNamesTemp = new HashSet<>(tagNames);
            tagNames = new ArrayList<>(newTagNamesTemp);

            addNewShopTag(newShop, tagNames);
        }

        if (shopAndMenuFilesDto.getThumbnail() != null) {

            UploadFile thumbnailFile = fileCloudStorage.storeFile(shopAndMenuFilesDto.getThumbnail());

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
            List<UploadFile> menuFileList = fileCloudStorage.storeFiles(menuMultipartFiles);
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

        //태그 삭제
        shop.detachAllShopTag();

        //저장되어있던 찜 삭제
        List<Zzim> zzimList = zzimRepository.findByShopId(id);

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
        double lon = shopXY[0];
        double lat = shopXY[1];

        // 카테고리 찾기
        Category category = categoryRepository.findByName(shopEditDto.getCategoryName())
                .orElseThrow(NoSuchCategoryException::new);

        // 상점 정보 수정
        shop.updateByDto(shopEditDto, category);
        shop.changeXY(lat, lon);

        List<Menu> menus = shop.getMenus();
        ShopFile shopFile = shop.getShopFile();

        // 등록된 실제 파일들 다 제거
        menuFilesDelete(menus);
        shopFileDelete(shopFile);

        menus.clear();
        shop.detachShopFile();

        List<MenuDto> menusDto = shopEditDto.getMenus();
        List<Menu> menuList = makeMenusWithFile(menusDto, shopAndMenuFilesDto);

        // 메뉴 삽입
        menuList.forEach(shop::addMenu);

        // 썸네일 삽입
        if (shopAndMenuFilesDto.getThumbnail() != null) {

            UploadFile thumbnailFile = fileCloudStorage.storeFile(shopAndMenuFilesDto.getThumbnail());

            ShopFile shopThumbnail = ShopFile.builder()
                    .shop(shop)
                    .file(thumbnailFile)
                    .desc(FileDesc.THUMBNAIL).build();

            shop.addShopFile(shopThumbnail);
        }

        // 태그 변경
        List<String> newTagNames = shopEditDto.getTags();
        List<ShopTag> originReviewTags = shop.getTags();
        List<ShopTag> deleteReviewTags = new ArrayList<>();


        // -- 새 태그와 비교해서 지워질 태그 삭제
        if (newTagNames != null && !newTagNames.isEmpty()) { // 새 태그가 존재
            Set<String> newTagNamesTemp = new HashSet<>(newTagNames);
            newTagNames = new ArrayList<>(newTagNamesTemp);
            if (!originReviewTags.isEmpty()) { // 기존 태그가 비어있지 않음
                for (ShopTag originReviewTag : originReviewTags) {
                    String tagName = originReviewTag.getTag().getName();
                    if (!newTagNames.contains(tagName)) {
                        deleteReviewTags.add(originReviewTag);
                    } else {
                        newTagNames.remove(tagName);
                    }
                }
            }

            for (ShopTag deletedShopTag : deleteReviewTags) {
                shop.detachShopTag(deletedShopTag);
            }

            // 새 태그 생성
            addNewShopTag(shop, newTagNames);
        } else { // 새 태그 비어있음
            if (!originReviewTags.isEmpty()) { // 기존 태그 존재
                shop.detachAllShopTag();
            }
        }

    }

    private void shopFileDelete(ShopFile shopFile) {
        if (shopFile != null) {
            fileCloudStorage.deleteFile(shopFile.getFile().getSavedName());
        }
    }

    private void menuFilesDelete(List<Menu> menus) {
        if (!menus.isEmpty()) {
            List<UploadFile> uploadFiles = menus.stream()
                    .map(Menu::getThumbnail)
                    .filter(Objects::nonNull)
                    .collect(Collectors.toList());
            uploadFiles.forEach(uploadFile -> fileCloudStorage.deleteFile(uploadFile.getSavedName()));
        }
    }

    private double[] findShopXY(String locDesc) {
        double[] result;
        try {
            result = naverMapApi.findShopXYApi(locDesc);
            // result[0] 이 경도(longitude) result[1]이 위도(latitude)
        } catch (IOException e) {
            throw new NaveMapApiException(e);
        }

        // 좌표를 찾지 못했을 시
        if (result[0] == 0) {
            throw new FindShopXYException();
        }

        return result;
    }

    public List<ShopReadDto> readMyShops(Member member) {
        List<Shop> shops = shopRepository.findByMemberWithFiles(member).orElseThrow(NoSuchShopException::new);
//        shops.forEach(Shop::getReviews); // 강제 초기화를 위함
        List<ShopReadDto> shopReadDtos = shops.stream().map(Shop::toShopReadDto).collect(Collectors.toList());
        shopReadDtos.forEach(this::readDtoAttachS3Link);

        return shopReadDtos;
    }

    public ShopReadDto readShop(Long shopId) {
        Shop shop = shopRepository.findByIdWithMemberAndFiles(shopId).orElseThrow(NoSuchShopException::new);
        ShopReadDto shopReadDto = shop.toShopReadDto();

        // 아직 썸네일과 메뉴 썸네일들이 s3 링크가 아닌 파일이름으로만 되어있음. 따라서 이를 s3링크로 변경해주어야함.
        readDtoAttachS3Link(shopReadDto);

        return shopReadDto;
    }

    // readDto에 설정된 이미지 이름들을 링크로 바꿔줌
    private void readDtoAttachS3Link(ShopReadDto shopReadDto) {
        // Shop thumb 주소 설정
        if(!shopReadDto.getThumbnail().equals("")){
            String thumbnailSavedName = shopReadDto.getThumbnail();
            String thumbUrl = fileCloudStorage.getUrl(thumbnailSavedName);
            shopReadDto.setThumbnail(thumbUrl);
        }

        // menu thumb 주소 설정
        List<MenuReadDto> menuDtos = shopReadDto.getMenus();
        menuDtos.forEach(m -> {
            if(!m.getThumbnail().equals("")){
                m.setThumbnail(fileCloudStorage.getUrl(m.getThumbnail()));
            }
        });
        shopReadDto.setMenus(menuDtos);

        // photo review 주소 설정
        List<ReviewReadDto> photoReviews = shopReadDto.getPhotoReviews();
        photoReviews.forEach(photoReview -> {
            List<String> photos = photoReview.getPhotos().stream().map(fileCloudStorage::getUrl).collect(Collectors.toList());
            photoReview.setPhotos(photos);
        });
    }

    // simpleDto에 설정된 이미지 이름들을 링크로 바꿔줌
    private void simpleDtoAttachS3Link(ShopSimpleDto shopSimpleDto) {
        if(!shopSimpleDto.getThumbnail().equals("")){
            String thumbnailSavedName = shopSimpleDto.getThumbnail();
            String thumbUrl = fileCloudStorage.getUrl(thumbnailSavedName);
            shopSimpleDto.setThumbnail(thumbUrl);
        }
    }

    private void addNewShopTag(Shop shop, List<String> newTagNames) {
        for (String newTagName : newTagNames) {
            Tag tag = tagRepository.findByName(newTagName).orElse(null);
            if (tag != null) {
                shop.getTags().add(new ShopTag(shop, tag));
            } else {
                Tag newTag = new Tag(newTagName);
                tagRepository.save(newTag); // 새 태그 저장
                shop.getTags().add(new ShopTag(shop, newTag));
            }
        }
    }

    public List<ShopSimpleDto> readSimpleShopsByPage(int page, int limit, double distance, double lat, double lon) {
        Pageable pageable = PageRequest.of(page, limit);
        List<Shop> shopsByDistance = shopRepository.findPageByDistance(pageable, distance, lat, lon);

        List<ShopSimpleDto> shopSimpleDtos = shopsByDistance.stream().map(Shop::toShopSimpleDto).collect(Collectors.toList());
        // Shop thumb 주소 설정
        shopSimpleDtos.forEach(this::simpleDtoAttachS3Link);

        return shopSimpleDtos;
    }

}
