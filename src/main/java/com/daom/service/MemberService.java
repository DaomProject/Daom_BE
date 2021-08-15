package com.daom.service;

import com.daom.domain.Member;
import com.daom.repository.MemberRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class MemberService {

    private final MemberRepository memberRepository;

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

    @Transactional
    public Member update(Long id, Member changeMember){
        Member originMember = memberRepository.findById(id).orElse(null);
        if ( originMember != null){
            originMember.setPassword(changeMember.getPassword());
            originMember.setUsername(changeMember.getUsername());
            originMember.setRole(changeMember.getRole());
        }

        return originMember;
    }
}
