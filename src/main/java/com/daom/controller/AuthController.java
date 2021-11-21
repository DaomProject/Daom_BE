package com.daom.controller;

import com.daom.config.auth.UserDetailsImpl;
import com.daom.config.jwt.JwtTokenProvider;
import com.daom.domain.Member;
import com.daom.dto.*;
import com.daom.dto.response.RestResponse;
import com.daom.exception.NotAuthorityThisJobException;
import com.daom.exception.UnmatchPasswordException;
import com.daom.service.MemberService;
import com.daom.service.ResponseService;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

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
    public RestResponse checkDupUsername(@RequestBody UsernameDupCheckDto dupCheckDto) {
        memberService.checkDupUsername(dupCheckDto.getUsername());
        return responseService.getSuccessResponse();
    }

    @PostMapping("/join/check/nickname")
    public RestResponse checkDupNickname(@RequestBody NicknameDupCheckDto dupCheckDto) {
        memberService.checkDupNickname(dupCheckDto.getNickname());
        return responseService.getSuccessResponse();
    }

    @PostMapping("/login")
    public RestResponse login(@RequestBody LoginDto loginDto) {
        Member member = memberService.findByUsername(loginDto.getUsername());
        if (!memberService.passwordMatch(loginDto.getPassword(), member.getPassword())) {
            throw new UnmatchPasswordException();
        }
        return responseService.getSingleResponse(jwtTokenProvider.createToken(member.getUsername(), member.getRole()));
    }

    @PutMapping("/updatepw/{id}")
    public RestResponse update(@PathVariable Long id, @AuthenticationPrincipal UserDetailsImpl userDetails, @RequestBody UpdatePwDto updatePwDto) {//로그인한 멤버 정보 받아오기
        Member member = memberService.findById(id);
        if (!member.getId().equals(userDetails.getMember().getId())) {
            throw new NotAuthorityThisJobException();//로그인한 사람만 할수있는일이니까
        }
        memberService.UpdatePassword(id, updatePwDto.getPassword());
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

    @GetMapping("/check")
    public RestResponse mailCheck(
            @RequestParam(value = "mail", required = true) String mail,
            @RequestParam(value = "username", required = false) String username) {
        boolean result = false;
        // mail만 -> 해당 메일로 가입된 회원 있는지 확인 ( ID찾기에 이용 )
        if (username != null) {
            result = memberService.checkJoinByUsernameAndMail(username, mail);
            return responseService.getSuccessResponse(Boolean.toString(result));
        }
        // id + mail -> 해당 ID와 메일로 가입된 회원 있는지 확인 ( PW찾기에 이용 )
        result = memberService.checkJoinByMail(mail);
        return responseService.getSuccessResponse(Boolean.toString(result));
    }
}
