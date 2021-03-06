package com.daom.config.auth;

import com.daom.domain.Member;
import com.daom.domain.Role;
import com.daom.repository.MemberRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class UserDetailsServiceImpl implements UserDetailsService {
    private final MemberRepository memberRepository;

    @Override
    @Transactional
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
        Member member = memberRepository.findByUsernameWithStudent(username).orElseThrow(() -> new UsernameNotFoundException("회원가입되어있지 않은 아이디입니다."));
        return new UserDetailsImpl(member);
    }
}
