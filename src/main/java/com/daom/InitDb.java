package com.daom;

import com.daom.domain.Category;
import com.daom.domain.Univ;
import com.daom.dto.MemberJoinDto;
import com.daom.dto.StudentJoinDto;
import com.daom.repository.CategoryRepository;
import com.daom.repository.MemberRepository;
import com.daom.repository.StudentRepository;
import com.daom.repository.UnivRepository;
import com.daom.service.MemberService;
import lombok.RequiredArgsConstructor;
import org.springframework.context.annotation.Profile;
import org.springframework.stereotype.Component;

import javax.annotation.PostConstruct;

@RequiredArgsConstructor
@Profile("localDB")
@Component
public class InitDb {

    private final UnivRepository univRepository;
    private final CategoryRepository categoryRepository;
    private final MemberService memberService;

    @PostConstruct
    public void init() {
        univInit();
        userInit();
    }

    public void univInit() {
        univRepository.save(new Univ("전북대학교"));
        categoryRepository.save(new Category("식당"));
    }

    public void userInit() {
        memberService.saveStudent(
                StudentJoinDto
                        .builder()
                        .username("test2")
                        .nickname("학생Test1")
                        .password("12345")
                        .univName("전북대학교")
                        .mail("test2@naver.com")
                        .admissionYear(2016L)
                        .tel("010-3333-2222")
                        .build());

        memberService.saveShop(
                MemberJoinDto.builder()
                        .username("test")
                        .password("12345")
                        .nickname("업체Test1")
                        .mail("test@naver.com")
                        .tel("010-2222-3333")
                        .build());
    }
}
