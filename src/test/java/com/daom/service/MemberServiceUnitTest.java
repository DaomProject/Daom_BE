package com.daom.service;

import com.daom.domain.Member;
import com.daom.domain.Role;
import com.daom.domain.Student;
import com.daom.domain.Univ;
import com.daom.dto.MemberJoinDto;
import com.daom.dto.StudentJoinDto;
import com.daom.exception.UnivNameNotFoundException;
import com.daom.exception.UsernameDuplicationException;
import com.daom.repository.MemberRepository;
import com.daom.repository.StudentRepository;
import com.daom.repository.UnivRepository;
import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.Spy;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.test.util.ReflectionTestUtils;
import org.springframework.test.web.servlet.result.MockMvcResultMatchers;

import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;

@ExtendWith(MockitoExtension.class)
class MemberServiceUnitTest{

    @InjectMocks
    MemberService memberService;

    @Mock
    private MemberRepository memberRepository;

    @Mock
    private StudentRepository studentRepository;

    @Mock
    private UnivRepository univRepository;

    @Spy
    PasswordEncoder passwordEncoder;

    @DisplayName("[학생] 회원가입 성공")
    @Test
    void saveStudent() {
        //given
        StudentJoinDto studentJoinDto = makeStudentJoinDto();

        final PasswordEncoder encoder = new BCryptPasswordEncoder();
        final String encodedPw = encoder.encode(studentJoinDto.getPassword());

        Member newMember = new Member(studentJoinDto.getUsername(), encodedPw, Role.STUDENT);
        Univ univ = new Univ("testUniv");

        //when
        Mockito.doReturn(Optional.empty()).when(memberRepository).findByUsername(studentJoinDto.getUsername());
        Mockito.doReturn(Optional.of(univ)).when(univRepository).findByName(studentJoinDto.getUnivName());

        Long savedMemberId = memberService.saveStudent(studentJoinDto);
        Mockito.doReturn(Optional.of(newMember)).when(memberRepository).findById(savedMemberId);

        //then
        Member findMember = memberService.findById(savedMemberId);
        assertThat(findMember.getUsername()).isEqualTo(studentJoinDto.getUsername());
        assertThat(encoder.matches("12345",encodedPw)).isTrue();

        //verify
        Mockito.verify(memberRepository,Mockito.times(1)).save(any(Member.class));
        Mockito.verify(studentRepository,Mockito.times(1)).save(any(Student.class));
        Mockito.verify(passwordEncoder,Mockito.times(1)).encode(any(String.class));
    }

    @DisplayName("[학생] 중복된 이름으로 회원가입 실패")
    @Test
    void saveStudentDupFail() {
        //given
        StudentJoinDto studentJoinDto = makeStudentJoinDto();

        final PasswordEncoder encoder = new BCryptPasswordEncoder();
        final String encodedPw = encoder.encode(studentJoinDto.getPassword());

        Member dupMember = new Member(studentJoinDto.getUsername(), encodedPw, Role.STUDENT);

        //when
        Mockito.doReturn(Optional.of(dupMember)).when(memberRepository).findByUsername(studentJoinDto.getUsername());

        //then
        assertThrows(UsernameDuplicationException.class, ()->{
            Long savedMemberId = memberService.saveStudent(studentJoinDto);
        });

        //verify
        Mockito.verify(passwordEncoder,Mockito.times(1)).encode(any(String.class));
        Mockito.verify(memberRepository,Mockito.times(1)).findByUsername(any(String.class));
    }

    @DisplayName("[학생] 존재하지 않는 대학 이름으로 회원가입 실패")
    @Test
    void saveUnivNotFoundFail() {
        //given
        StudentJoinDto studentJoinDto = makeStudentJoinDto();

        //when
        Mockito.doReturn(Optional.empty()).when(memberRepository).findByUsername(studentJoinDto.getUsername());
        Mockito.doReturn(Optional.empty()).when(univRepository).findByName(studentJoinDto.getUnivName());

        //then
        assertThrows(UnivNameNotFoundException.class, ()-> memberService.saveStudent(studentJoinDto));

        //verify
        Mockito.verify(passwordEncoder,Mockito.times(1)).encode(any(String.class));
        Mockito.verify(memberRepository,Mockito.times(1)).findByUsername(any(String.class));
    }


    @DisplayName("[업체] 회원가입 성공")
    @Test
    public void saveShop() throws Exception {
        //given
        MemberJoinDto memberJoinDto = makeMemberJoinDto();
        PasswordEncoder encoder = new BCryptPasswordEncoder();
        String encodedPw = encoder.encode(memberJoinDto.getPassword());

        Member newMember = new Member(memberJoinDto.getUsername(), encodedPw, Role.SHOP);

        //when
        Mockito.doReturn(Optional.empty()).when(memberRepository).findByUsername(memberJoinDto.getUsername());

        Long savedMemberId = memberService.saveShop(memberJoinDto);
        Mockito.doReturn(Optional.of(newMember)).when(memberRepository).findById(savedMemberId);

        //then
        Member findMember = memberService.findById(savedMemberId);
        assertThat(findMember.getUsername()).isEqualTo(memberJoinDto.getUsername());
        assertThat(encoder.matches("12345" ,encodedPw)).isTrue();

        //verify
        Mockito.verify(memberRepository,Mockito.times(1)).save(any(Member.class));
        Mockito.verify(passwordEncoder,Mockito.times(1)).encode(any(String.class));

    }

    @DisplayName("[업체] 중복된 이름으로 회원가입 실패")
    @Test
    void saveShopDupFail() {
        //given
        MemberJoinDto memberJoinDto = makeMemberJoinDto();

        final PasswordEncoder encoder = new BCryptPasswordEncoder();
        final String encodedPw = encoder.encode(memberJoinDto.getPassword());

        Member newMember = new Member(memberJoinDto.getUsername(), encodedPw, Role.STUDENT);

        //when
        Mockito.doReturn(Optional.of(newMember)).when(memberRepository).findByUsername(memberJoinDto.getUsername());

        //then
        assertThrows(UsernameDuplicationException.class, ()->{
            Long savedMemberId = memberService.saveShop(memberJoinDto);
        });

        //verify
        Mockito.verify(passwordEncoder,Mockito.times(1)).encode(any(String.class));
        Mockito.verify(memberRepository,Mockito.times(1)).findByUsername(any(String.class));
    }

    private MemberJoinDto makeMemberJoinDto(){
        return MemberJoinDto.builder()
                .username("test2")
                .nickname("test")
                .password("12345").build();
    }

    private StudentJoinDto makeStudentJoinDto() {
        return StudentJoinDto.builder()
                .username("test")
                .password("12345")
                .nickname("testNickname")
                .admissionYear(2016L)
                .univName("testUniv")
                .build();
    }
}