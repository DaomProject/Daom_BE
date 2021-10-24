package com.daom.service;


import com.daom.domain.Shop;
import com.daom.domain.Student;
import com.daom.domain.StudentLikeShop;
import com.daom.domain.Zzim;
import com.daom.exception.*;
import com.daom.repository.ShopRepository;
import com.daom.repository.StudentLikeShopRepository;
import com.daom.repository.StudentRepository;
import com.daom.repository.ZzimRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@RequiredArgsConstructor
@Service
@Transactional(readOnly = true)
public class LikeService {

    private final StudentRepository studentRepository;
    private final ShopRepository shopRepository;
    private final StudentLikeShopRepository likeRepository;

    // 좋아요 하기
    @Transactional
    public void like(Long studentId, Long shopId) {
        if(studentId == null){
            // 만약 학생계정이 아니라면 찜하지 못한다.
            throw new NotAuthorityThisJobException();
        }
        Student student = studentRepository.findById(studentId).orElseThrow(NoSuchStudentException::new);
        Shop shop = shopRepository.findById(shopId).orElseThrow(NoSuchShopException::new);


        StudentLikeShop existLikeShop = likeRepository.findByStudentIdAndShopId(studentId, shopId).orElse(null);

        // 중복생성 방지 로직
        if (existLikeShop == null) {
            StudentLikeShop like = new StudentLikeShop(student, shop);
            likeRepository.save(like);
            shop.plusLikeNum();
        }

    }

    // 좋아요 취소
    @Transactional
    public void unLike(Long studentId, Long shopId) {
        Shop shop = shopRepository.findById(shopId).orElseThrow(NoSuchShopException::new);
        StudentLikeShop likeShop = likeRepository.findByStudentIdAndShopId(studentId, shopId)
                .orElseThrow(NoSuchLikeException::new);

        likeRepository.delete(likeShop);
        shop.minusLikeNum();

    }
}