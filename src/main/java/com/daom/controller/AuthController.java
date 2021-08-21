package com.daom.controller;

import com.daom.config.jwt.JwtTokenProvider;
import com.daom.domain.Member;
import com.daom.dto.LoginDto;
import com.daom.dto.StudentJoinDto;
import com.daom.dto.response.RestResponse;
import com.daom.service.MemberService;
import com.daom.service.ResponseService;
import lombok.RequiredArgsConstructor;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequiredArgsConstructor
@RequestMapping("/auth")
public class AuthController {
    private final ResponseService responseService;
    private final MemberService memberService;
    private final JwtTokenProvider jwtTokenProvider;
    private final PasswordEncoder passwordEncoder;


    @PostMapping("/join/student")
    public RestResponse joinStudent(@RequestBody StudentJoinDto studentJoinDto) {
        //패스워드 인코딩
        String encodedPassword = passwordEncoder.encode(studentJoinDto.getPassword());
        studentJoinDto.setPassword(encodedPassword);

        memberService.saveStudent(studentJoinDto);
        return responseService.getSuccessResponse();
    }

    @PostMapping("/login")
    public RestResponse login(@RequestBody LoginDto loginDto) {
        Member member = memberService.findByUsername(loginDto.getUsername());
        if (!passwordEncoder.matches(loginDto.getPassword(), member.getPassword())) {
            throw new IllegalArgumentException("잘못된 비밀번호입니다.");
        }
        return responseService.getSingleResponse(jwtTokenProvider.createToken(member.getUsername(), member.getRole()));
    }

}
