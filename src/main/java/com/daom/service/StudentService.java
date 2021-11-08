package com.daom.service;

import com.daom.domain.*;
import com.daom.dto.MyInfoStudentDto;
import com.daom.dto.ShopSimpleDto;
import com.daom.exception.NoSuchStudentException;
import com.daom.repository.MemberRepository;
import com.daom.repository.StudentLikeShopRepository;
import com.daom.repository.StudentRepository;
import com.daom.repository.ZzimRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class StudentService {
    private final FileCloudStorage fileCloudStorage;
    private final MemberRepository memberRepository;
    private final StudentRepository studentRepository;
    private final StudentLikeShopRepository studentLikeShopRepository;
    private final ZzimRepository zzimRepository;

    @Transactional
    public void profileUpload(Long studentId, MultipartFile thumbnail) {
        Student student = studentRepository.findById(studentId).orElseThrow(NoSuchStudentException::new);
        if (student.getThumbnailName() != null) {
            profileDelete(studentId);
        }
        UploadFile uploadedThumbnail = fileCloudStorage.storeFile(thumbnail);

        student.addThumbnail(uploadedThumbnail);

    }

    @Transactional
    public void profileDelete(Long studentId) {
        Student student = studentRepository.findById(studentId).orElseThrow(NoSuchStudentException::new);
        fileCloudStorage.deleteFile(student.getThumbnailName());
        student.deleteThumbnail();

    }

    public MyInfoStudentDto myInfo(Long memberId) {
        Member member = memberRepository.findById(memberId).orElseThrow(NoSuchStudentException::new);
        return new MyInfoStudentDto(member);
    }

    public List<ShopSimpleDto> readMyLikeShop(Student student, int page, int limit) {
        Pageable pageable = PageRequest.of(page, limit);

        List<StudentLikeShop> list
                = studentLikeShopRepository.findByStudent(pageable,student);
        List<ShopSimpleDto> shops = new ArrayList<>();

        if (!list.isEmpty()) {
            shops = list.stream()
                    .map(StudentLikeShop::getShop)
                    .map(Shop::toShopSimpleDto)
                    .collect(Collectors.toList());
        }

        return shops;

    }

    public List<ShopSimpleDto> readMyZzimShop(Student student, int page, int limit) {
        Pageable pageable = PageRequest.of(page, limit);

        List<Zzim> zzims =
                zzimRepository.findByStudent(pageable, student);
        List<ShopSimpleDto> shops = new ArrayList<>();

        if (!zzims.isEmpty()) {
            shops = zzims.stream()
                    .map(Zzim::getShop)
                    .map(Shop::toShopSimpleDto)
                    .collect(Collectors.toList());
        }

        return shops;

    }
}
