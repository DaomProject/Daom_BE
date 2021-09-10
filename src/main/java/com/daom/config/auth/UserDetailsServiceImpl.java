package com.daom.config.auth;

import com.daom.domain.Member;
import com.daom.domain.Role;
import com.daom.domain.Shop;
import com.daom.exception.NoSuchUserException;
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
    @Transactional(readOnly = true)
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
        Member member = memberRepository.findByUsername(username).orElseThrow(() -> new UsernameNotFoundException("회원가입되어있지 않은 아이디입니다."));

        if(member.getRole() == Role.STUDENT){
            member.getStudent().getUniv().getName();
        }
        return new UserDetailsImpl(member);
    }
}
