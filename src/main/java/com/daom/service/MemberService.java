package com.daom.service;

import com.daom.domain.Member;
import com.daom.domain.Role;
import com.daom.domain.Student;
import com.daom.dto.MemberJoinDto;
import com.daom.dto.StudentJoinDto;
import com.daom.exception.NoSuchUserException;
import com.daom.exception.UsernameDuplicationException;
import com.daom.repository.MemberRepository;
import com.daom.repository.StudentRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class MemberService {

    private final MemberRepository memberRepository;
    private final StudentRepository studentRepository;
    private final PasswordEncoder passwordEncoder;

    @Transactional
    public Long saveStudent(StudentJoinDto studentJoinDto) {
        String encodedPassword = passwordEncoder.encode(studentJoinDto.getPassword());
        studentJoinDto.setPassword(encodedPassword);

        Member existMember = memberRepository.findByUsername(studentJoinDto.getUsername()).orElse(null);

        if (existMember != null) {
            //기존에 회원 이름과 동일한 회원이 존재한다면
            throw new UsernameDuplicationException();
        }

        Member newMember = new Member(studentJoinDto.getUsername(), studentJoinDto.getPassword(), Role.STUDENT);
        Student newStudent = Student.builder()
                .member(newMember)
                .admissionYear(studentJoinDto.getAdmissionYear())
                .univName(studentJoinDto.getUnivName())
                .nickname(studentJoinDto.getNickname())
                .build();

        memberRepository.save(newMember);
        studentRepository.save(newStudent);
        return newMember.getId();
    }

    // Shop 회원가입은 오직 Member에 관한 정보만 받기 때문에 이를 이용한다.
    @Transactional
    public Long saveShop(MemberJoinDto memberJoinDto) {
        String encodedPassword = passwordEncoder.encode(memberJoinDto.getPassword());
        memberJoinDto.setPassword(encodedPassword);

        Member existMember = memberRepository.findByUsername(memberJoinDto.getUsername()).orElse(null);

        if (existMember != null) {
            //기존에 회원 이름과 동일한 회원이 존재한다면
            throw new UsernameDuplicationException();
        }

        Member newMember = new Member(memberJoinDto.getUsername(), memberJoinDto.getPassword(), Role.SHOP);

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
//    @Transactional
//    public Member update(Long id, Member changeMember){
//        Member originMember = memberRepository.findById(id).orElse(null);
//        if ( originMember != null){
//            originMember.setPassword(changeMember.getPassword());
//            originMember.setUsername(changeMember.getUsername());
//            originMember.setRole(changeMember.getRole());
//        }
//
//        return originMember;
//    }
}
