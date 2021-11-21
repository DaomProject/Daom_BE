package com.daom.controller;

import com.daom.config.auth.UserDetailsImpl;
import com.daom.config.jwt.JwtTokenProvider;
import com.daom.domain.Member;
import com.daom.dto.UpdatePwDto;
import com.daom.dto.response.RestResponse;
import com.daom.exception.NotAuthorityThisJobException;
import com.daom.service.MemberService;
import com.daom.service.ResponseService;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

@RestController
@RequiredArgsConstructor
@RequestMapping("/member")
public class MemberController {
    private final ResponseService responseService;
    private final MemberService memberService;

    @PutMapping("/password")
    public RestResponse update(@AuthenticationPrincipal UserDetailsImpl userDetails, @RequestBody UpdatePwDto updatePwDto) {//로그인한 멤버 정보 받아오기
        Long memberId = userDetails.getMember().getId();
        memberService.updatePassword(memberId, updatePwDto.getPassword());
        return responseService.getSuccessResponse();

    }

    @GetMapping("/is-student")
    public RestResponse isStudent(@AuthenticationPrincipal UserDetailsImpl userDetails) {
        Member member = userDetails.getMember();

        if (member.getStudent() == null) {
            return responseService.getSuccessResponse("shop");
        } else {
            return responseService.getSuccessResponse("student");
        }
    }
}
