package com.daom.repository;

import com.daom.domain.Member;
import org.springframework.data.jpa.repository.EntityGraph;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.Optional;

// SPRING JPA --쉽게 --> Spring DATA JPA
public interface MemberRepository extends JpaRepository<Member, Long> {

    @Query("select m from Member m" +
            " left join fetch m.student s" +
            " where m.username = :username")
    Optional<Member> findByUsernameWithStudent(@Param("username") String username);
    Optional<Member> findByUsername(String username);
    Optional<Member> findByNickname(String nickname);

    @EntityGraph(attributePaths = {"student"}, type = EntityGraph.EntityGraphType.LOAD)
    Optional<Member> findWithStudentById(Long memberId);
}
