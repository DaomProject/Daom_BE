package com.daom.repository;

import com.daom.domain.Member;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

// SPRING JPA --쉽게 --> Spring DATA JPA
public interface MemberRepository extends JpaRepository<Member, Long> {
    Optional<Member> findByUsername(String username);
    Optional<Member> findByNickname(String nickname);
}
