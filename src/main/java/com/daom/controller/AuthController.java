package com.daom.controller;

import com.daom.config.jwt.JwtTokenProvider;
import com.daom.domain.Member;
import com.daom.dto.*;
import com.daom.dto.response.RestResponse;
import com.daom.service.MemberService;
import com.daom.service.ResponseService;
import lombok.RequiredArgsConstructor;
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


    @PostMapping("/join/student")
    public RestResponse joinStudent(@RequestBody StudentJoinDto studentJoinDto) {
        memberService.saveStudent(studentJoinDto);
        return responseService.getSuccessResponse();
    }

    @PostMapping("/join/shop")
    public RestResponse joinShop(@RequestBody MemberJoinDto memberJoinDto) {
        memberService.saveShop(memberJoinDto);
        return responseService.getSuccessResponse();
    }

    @PostMapping("/join/check/username")
    public RestResponse checkDupUsername(@RequestBody UsernameDupCheckDto dupCheckDto){
        memberService.checkDupUsername(dupCheckDto.getUsername());
        return responseService.getSuccessResponse();
    }

    @PostMapping("/join/check/nickname")
    public RestResponse checkDupNickname(@RequestBody NicknameDupCheckDto dupCheckDto){
        memberService.checkDupNickname(dupCheckDto.getNickname());
        return responseService.getSuccessResponse();
    }

    @PostMapping("/login")
    public RestResponse login(@RequestBody LoginDto loginDto) {
        Member member = memberService.findByUsername(loginDto.getUsername());
        if (!memberService.passwordMatch(loginDto.getPassword(), member.getPassword())) {
            throw new IllegalArgumentException("잘못된 비밀번호입니다.");
        }
        return responseService.getSingleResponse(jwtTokenProvider.createToken(member.getUsername(), member.getRole()));
    }

}
