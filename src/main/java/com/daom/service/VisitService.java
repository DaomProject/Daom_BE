package com.daom.service;

import com.daom.domain.*;
import com.daom.exception.NoSuchShopException;
import com.daom.exception.NotAuthorityThisJobException;
import com.daom.repository.ShopRepository;
import com.daom.repository.StudentVisitShopRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;

@RequiredArgsConstructor
@Transactional(readOnly = true)
@Service
public class VisitService {
    private final StudentVisitShopRepository studentVisitShopRepository;
    private final ShopRepository shopRepository;

    public boolean isVisit(Member loginMember, Long shopId) {
        Shop shop = shopRepository.findById(shopId).orElseThrow(NoSuchShopException::new);

        if (loginMember.getRole() != Role.STUDENT) {
            throw new NotAuthorityThisJobException();
        }
        Student student = loginMember.getStudent();

        StudentVisitShop studentVisitShop = studentVisitShopRepository.findByStudentAndShop(student, shop).orElse(null);
        return studentVisitShop != null;
    }

    @Transactional
    public void visit(Member loginMember, Long shopId) {
        Shop shop = shopRepository.findById(shopId).orElseThrow(NoSuchShopException::new);

        if (loginMember.getRole() != Role.STUDENT) {
            throw new NotAuthorityThisJobException();
        }
        Student student = loginMember.getStudent();

        StudentVisitShop studentVisitShop = StudentVisitShop.builder()
                .shop(shop)
                .student(student)
                .visitDate(LocalDateTime.now())
                .build();

        studentVisitShopRepository.save(studentVisitShop);
    }
}
