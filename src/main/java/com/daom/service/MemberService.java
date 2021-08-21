package com.daom.service;

import com.daom.domain.Member;
import com.daom.domain.Role;
import com.daom.domain.Student;
import com.daom.dto.StudentJoinDto;
import com.daom.exception.NoSuchUserException;
import com.daom.repository.MemberRepository;
import com.daom.repository.StudentRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class MemberService {

    private final MemberRepository memberRepository;
    private final StudentRepository studentRepository;

    @Transactional
    public Long saveStudent(StudentJoinDto studentJoinDto){
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

    @Transactional
    public void save(Member member){
        memberRepository.save(member);
    }

    @Transactional
    public void delete(Member member){
        memberRepository.delete(member);
    }

    public Member findById(Long memberId){
        return memberRepository.findById(memberId).orElse(null);
    }

    public Member findByUsername(String username){
        return memberRepository.findByUsername(username).orElseThrow(NoSuchUserException::new);
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
