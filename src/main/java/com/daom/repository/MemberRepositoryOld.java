package com.daom.repository;

import com.daom.domain.Member;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Repository;

import javax.persistence.EntityManager;

@Repository
@RequiredArgsConstructor
public class MemberRepositoryOld {
    private final EntityManager em;

    public void save(Member member){
        em.persist(member);
    }

    public void delete(Member member){
        em.remove(member);
    }

    public Member findById(Long memberId){
        Member findMember = em.find(Member.class, memberId);

        return findMember;
    }
}
