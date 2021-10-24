package com.daom.service;

import com.daom.domain.Shop;
import com.daom.domain.Student;
import com.daom.domain.Zzim;
import com.daom.exception.NoSuchShopException;
import com.daom.exception.NoSuchStudentException;
import com.daom.exception.NoSuchZzimException;
import com.daom.exception.NotAuthorityThisJobException;
import com.daom.repository.ShopRepository;
import com.daom.repository.StudentRepository;
import com.daom.repository.ZzimRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@RequiredArgsConstructor
@Service
@Transactional(readOnly = true)
public class ZzimService {

    private final StudentRepository studentRepository;
    private final ShopRepository shopRepository;
    private final ZzimRepository zzimRepository;

    // 찜하기
    @Transactional
    public void saveZzim(Long studentId, Long shopId) {
        if(studentId == null){
            // 만약 학생계정이 아니라면 찜하지 못한다.
            throw new NotAuthorityThisJobException();
        }
        Student student = studentRepository.findById(studentId).orElseThrow(NoSuchStudentException::new);
        Shop shop = shopRepository.findById(shopId).orElseThrow(NoSuchShopException::new);


        Zzim existZzim = zzimRepository.findByStudentIdAndShopId(studentId, shopId).orElse(null);

        // 중복생성 방지 로직
        if (existZzim == null) {
            Zzim zzim = new Zzim(student, shop);
            zzimRepository.save(zzim);
            shop.plusZzimNum();

        }

    }

    // 찜 삭제
    @Transactional
    public void deleteZzim(Long studentId, Long shopId) {
        Shop shop = shopRepository.findById(shopId).orElseThrow(NoSuchShopException::new);
        Zzim zzim = zzimRepository.findByStudentIdAndShopId(studentId, shopId)
                .orElseThrow(NoSuchZzimException::new);

        zzimRepository.delete(zzim);
        shop.minusZzimNum();

    }
}

