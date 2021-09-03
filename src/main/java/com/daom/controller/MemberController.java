package com.daom.controller;

import com.daom.config.auth.UserDetailsImpl;
import com.daom.config.jwt.JwtTokenProvider;
import com.daom.domain.Member;
import com.daom.dto.LoginDto;
import com.daom.dto.MyInfoStudentDto;
import com.daom.dto.StudentJoinDto;
import com.daom.dto.response.RestResponse;
import com.daom.dto.response.SingleResponse;
import com.daom.service.MemberService;
import com.daom.service.ResponseService;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.bind.annotation.*;

@RestController
@RequiredArgsConstructor
@RequestMapping("/member")
public class MemberController {

    private final ResponseService responseService;
    private final MemberService memberService;

//    @PostMapping
//    public String save(@RequestBody Member member) {
//        memberService.save(member);
//        return "Done";
//    }

    @GetMapping("/me")
    public RestResponse myInfo(@AuthenticationPrincipal UserDetailsImpl userDetails){
        Member member = userDetails.getMember();
        return responseService.getSingleResponse(memberService.myInfo(member));
    }

    @GetMapping("/{id}")
    public Member findById(@PathVariable Long id) {
        return memberService.findById(id);
    }

    @DeleteMapping("/{id}")
    public String delete(@PathVariable Long id) {
        Member findMember = memberService.findById(id);
        memberService.delete(findMember);

        return "Done";
    }

//    @PutMapping("/{id}")
//    public Member update(@PathVariable Long id, @RequestBody Member changeMember) {
//        return memberService.update(id, changeMember);
//    }
}
