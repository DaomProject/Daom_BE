package com.daom.service;

import com.daom.domain.*;
import com.daom.dto.MailDto;
import com.daom.dto.MemberJoinDto;
import com.daom.dto.StudentJoinDto;
import com.daom.exception.*;
import com.daom.repository.*;
import com.daom.utils.RandomKeyGenerator;
import lombok.RequiredArgsConstructor;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class MemberService {

    private final MemberRepository memberRepository;
    private final PasswordEncoder passwordEncoder;
    private final UnivRepository univRepository;


    @Transactional
    public Long saveStudent(StudentJoinDto studentJoinDto) {
        // 비밀번호 인코딩
        String encodedPassword = passwordEncoder.encode(studentJoinDto.getPassword());

        checkDupUsername(studentJoinDto.getUsername());
        checkDupNickname(studentJoinDto.getNickname());
        checkDupEmail(studentJoinDto.getMail());

        // 멤버 생성
        Member newMember = Member.builder()
                .username(studentJoinDto.getUsername())
                .password(encodedPassword)
                .nickname(studentJoinDto.getNickname())
                .tel(studentJoinDto.getTel())
                .role(Role.STUDENT)
                .mail(studentJoinDto.getMail())
                .build();

        // 학교 조회
        Univ findUniv = univRepository.findByName(studentJoinDto.getUnivname()).orElseThrow(UnivNameNotFoundException::new);

        Student newStudent = Student.builder()
                .member(newMember)
                .admissionYear(studentJoinDto.getAdmissionYear())
                .univ(findUniv)
                .build();

        newMember.connectStudent(newStudent);

        memberRepository.save(newMember);
        return newMember.getId();
    }

    // Shop 회원가입은 오직 Member에 관한 정보만 받기 때문에 이를 이용한다.
    @Transactional
    public Long saveShop(MemberJoinDto memberJoinDto) {
        String encodedPassword = passwordEncoder.encode(memberJoinDto.getPassword());

        checkDupUsername(memberJoinDto.getUsername());
        checkDupNickname(memberJoinDto.getNickname());
        checkDupEmail(memberJoinDto.getMail());

        Member newMember = Member.builder()
                .username(memberJoinDto.getUsername())
                .password(encodedPassword)
                .nickname(memberJoinDto.getNickname())
                .tel(memberJoinDto.getTel())
                .role(Role.SHOP)
                .mail(memberJoinDto.getMail())
                .build();

        memberRepository.save(newMember);
        return newMember.getId();
    }

    @Transactional
    public String setTempPassword(MailDto mailDto){
        Member member = memberRepository.findByMail(mailDto.getMail()).orElseThrow(NoSuchMemberException::new);
        RandomKeyGenerator randomKeyGenerator = new RandomKeyGenerator();
        String randomPassword = randomKeyGenerator.getRamdomPassword(10);
        String encodedPassword = passwordEncoder.encode(randomPassword);

        member.changePw(encodedPassword);
        return randomPassword;
    }

    @Transactional
    public void delete(Member member) {
        memberRepository.delete(member);
    }

    public Member findById(Long memberId) {
        return memberRepository.findById(memberId).orElse(null);
    }

    public Member findByUsername(String username) {
        return memberRepository.findByUsername(username).orElseThrow(NoSuchMemberException::new);
    }

    public boolean passwordMatch(String savedPassword, String enteredPassword) {
        return passwordEncoder.matches(savedPassword, enteredPassword);
    }

    // 중복 확인 ( 닉네임, 아이디, 이메일)
    public void checkDupNickname(String nickname) {
        Member existMember = memberRepository.findByNickname(nickname).orElse(null);

        if (existMember != null) {
            //기존에 회원 이름과 동일한 회원이 존재한다면
            throw new NicknameDuplicationException();
        }
    }

    public void checkDupUsername(String username) {
        Member existMember = memberRepository.findByUsername(username).orElse(null);

        if (existMember != null) {
            //기존에 회원 이름과 동일한 회원이 존재한다면
            throw new UsernameDuplicationException();
        }
    }

    public void checkDupEmail(String mail){
        Member existMember = memberRepository.findByMail(mail).orElse(null);

        if(existMember != null){
            throw new MailDuplicationException();
        }
    }

    @Transactional
    public Member updatePassword(Long memberId, String newPw) {
        Member existMember = memberRepository.findById(memberId).orElseThrow(NoSuchMemberException::new);
        String encodedPassword = passwordEncoder.encode(newPw);
        existMember.changePw(encodedPassword);

        return existMember;
    }

    public boolean checkJoinByMail(String mail) {
        Member member = memberRepository.findByMail(mail).orElse(null);
        if (member == null) {
            return false;
        }
        return true;
    }

    public boolean checkJoinByUsernameAndMail(String username, String mail) {
        Member member = memberRepository.findByUsernameAndMail(username, mail).orElse(null);
        if (member == null) {
            return false;
        }
        return true;
    }

    public String getUsernameByMail(String mail) {
        Member member = memberRepository.findByMail(mail).orElseThrow(NoSuchMemberException::new);
        return member.getUsername();
    }
}
