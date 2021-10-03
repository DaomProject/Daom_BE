package com.daom.controller;

import com.daom.config.auth.UserDetailsImpl;
import com.daom.domain.Member;
import com.daom.dto.response.RestResponse;
import com.daom.service.MemberService;
import com.daom.service.ResponseService;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

@RestController
@RequiredArgsConstructor
@RequestMapping("/student")
public class StudentController {

    private final ResponseService responseService;
    private final MemberService memberService;

    // 학생 마이페이지 조회
    @GetMapping("/me")
    public RestResponse myInfo(@AuthenticationPrincipal UserDetailsImpl userDetails){
        Member member = userDetails.getMember();
        return responseService.getSingleResponse(memberService.myInfo(member));
    }

//    @GetMapping("/{id}")
//    public Member findById(@PathVariable Long id) {
//        return memberService.findById(id);
//    }
//
//    @DeleteMapping("/{id}")
//    public String delete(@PathVariable Long id) {
//        Member findMember = memberService.findById(id);
//        memberService.delete(findMember);
//
//        return "Done";
//    }

//    @PutMapping("/{id}")
//    public Member update(@PathVariable Long id, @RequestBody Member changeMember) {
//        return memberService.update(id, changeMember);
//    }
}
