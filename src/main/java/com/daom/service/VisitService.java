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
import java.util.Comparator;
import java.util.List;
import java.util.stream.Collectors;

@RequiredArgsConstructor
@Transactional(readOnly = true)
@Service
public class VisitService {
    private final StudentVisitShopRepository studentVisitShopRepository;
    private final ShopRepository shopRepository;

    public boolean isVisitInPeriod(Member loginMember, Long shopId, int period) {
        List<StudentVisitShop> visitInPeriod = getVisitInPeriod(loginMember, shopId, period);
        return !visitInPeriod.isEmpty();
    }

    public List<StudentVisitShop> getVisitInPeriod(Member loginMember, Long shopId, int period) {
        Shop shop = shopRepository.findById(shopId).orElseThrow(NoSuchShopException::new);

        if (loginMember.getRole() != Role.STUDENT) {
            throw new NotAuthorityThisJobException();
        }
        Student student = loginMember.getStudent();

        List<StudentVisitShop> studentVisitShopList = studentVisitShopRepository.findAllByStudentAndShop(student, shop);
        LocalDateTime now = LocalDateTime.now();

        return studentVisitShopList.stream()
                .filter(studentVisitShop -> now.isAfter(studentVisitShop.getVisitDate())
                        && now.isBefore(studentVisitShop.getVisitDate().plusDays(period)))
                .sorted(Comparator.comparing(StudentVisitShop::getVisitDate))
                .collect(Collectors.toList());
    }

    public List<StudentVisitShop> getVisitCanBeReviewed(Member loginMember, Long shopId, int period) {
        Shop shop = shopRepository.findById(shopId).orElseThrow(NoSuchShopException::new);

        if (loginMember.getRole() != Role.STUDENT) {
            throw new NotAuthorityThisJobException();
        }
        Student student = loginMember.getStudent();

        List<StudentVisitShop> studentVisitShopList = studentVisitShopRepository.findAllByStudentAndShop(student, shop);
        LocalDateTime now = LocalDateTime.now();

        return studentVisitShopList.stream()
                .filter(studentVisitShop -> studentVisitShop.canReview(period))
                .sorted(Comparator.comparing(StudentVisitShop::getVisitDate))
                .collect(Collectors.toList());
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
