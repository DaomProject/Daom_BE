package com.daom.service;

import com.daom.domain.*;
import com.daom.dto.MemberJoinDto;
import com.daom.dto.MyInfoStudentDto;
import com.daom.dto.StudentJoinDto;
import com.daom.exception.*;
import com.daom.repository.*;
import lombok.RequiredArgsConstructor;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class MemberService {

    private final MemberRepository memberRepository;
    private final PasswordEncoder passwordEncoder;
    private final UnivRepository univRepository;

    private final FileStorage fileStorage;
    private final UploadFileRepository uploadFileRepository;
    private final StudentRepository studentRepository;

    @Transactional
    public Long saveStudent(StudentJoinDto studentJoinDto) {
        // 비밀번호 인코딩
        String encodedPassword = passwordEncoder.encode(studentJoinDto.getPassword());

        checkDupUsername(studentJoinDto.getUsername());
        checkDupNickname(studentJoinDto.getNickname());

        // 멤버 생성
        Member newMember = Member.builder()
            .username(studentJoinDto.getUsername())
            .password(encodedPassword)
            .nickname(studentJoinDto.getNickname())
            .tel(studentJoinDto.getTel())
            .role(Role.STUDENT)
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

        Member newMember = Member.builder()
                .username(memberJoinDto.getUsername())
                .password(encodedPassword)
                .nickname(memberJoinDto.getNickname())
                .tel(memberJoinDto.getTel())
                .role(Role.SHOP)
                .build();

        memberRepository.save(newMember);
        return newMember.getId();
    }

    @Transactional
    public void delete(Member member) {
        memberRepository.delete(member);
    }

    public Member findById(Long memberId) {
        return memberRepository.findById(memberId).orElse(null);
    }

    public Member findByUsername(String username) {
        return memberRepository.findByUsername(username).orElseThrow(NoSuchUserException::new);
    }

    public boolean passwordMatch(String savedPassword, String enteredPassword) {
        return passwordEncoder.matches(savedPassword, enteredPassword);
    }

    // 중복 확인 ( 닉네임, 아이디 )
    public void checkDupNickname(String nickname){
        Member existMember = memberRepository.findByNickname(nickname).orElse(null);

        if (existMember != null) {
            //기존에 회원 이름과 동일한 회원이 존재한다면
            throw new NicknameDuplicationException();
        }
    }

    public void checkDupUsername(String username){
        Member existMember = memberRepository.findByUsername(username).orElse(null);

        if (existMember != null) {
            //기존에 회원 이름과 동일한 회원이 존재한다면
            throw new UsernameDuplicationException();
        }
    }

    public MyInfoStudentDto myInfo(Member member) {
        return new MyInfoStudentDto(member);
    }
    @Transactional
    public Member UpdatePassword(Long memberId, String newPw) {
        Member existMember = memberRepository.findById(memberId).orElse(null);
        String encodedPassword = passwordEncoder.encode(newPw);
        if (existMember == null) {
            //기존에 회원 이름과 동일한 회원이 존재하지 않는다면
            throw new NoSuchUserException();
        }
        existMember.changePw(encodedPassword);

        return existMember;

    }
    @Transactional
    public void profileUpload(Long studentId, MultipartFile thumbnail) {
        Student student = studentRepository.findById(studentId).orElseThrow(NoSuchStudentException::new);
        UploadFile uploadedThumbnail = fileStorage.storeFile(thumbnail);
        // DB에저장을 해야함
        // 1. Repository에서 저장
//        uploadFileRepository.save(uploadedThumbnail);
        // 2. cascade 사용

        student.addThumbnail(uploadedThumbnail);

    }

}
