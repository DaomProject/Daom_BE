package com.daom.controller;

import com.daom.domain.Member;
import com.daom.service.MemberService;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

@RestController
@RequiredArgsConstructor
@RequestMapping("/member")
public class MemberController {

    private final MemberService memberService;

    @PostMapping
    public String save(@RequestBody Member member){
        memberService.save(member);
        return "Done";
    }

    @GetMapping("/{id}")
    public Member findById(@PathVariable Long id){
        return memberService.findById(id);
    }

    @DeleteMapping("/{id}")
    public String delete(@PathVariable Long id){
        Member findMember = memberService.findById(id);
        memberService.delete(findMember);

        return "Done";
    }

    @PutMapping("/{id}")
    public Member update(@PathVariable Long id, @RequestBody Member changeMember){
        return memberService.update(id, changeMember);
    }
}
