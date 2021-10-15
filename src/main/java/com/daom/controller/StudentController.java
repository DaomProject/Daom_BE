package com.daom.controller;

import com.daom.config.auth.UserDetailsImpl;
import com.daom.domain.Member;
import com.daom.domain.Student;
import com.daom.dto.LoginDto;
import com.daom.dto.response.RestResponse;
import com.daom.exception.NotAuthorityThisJobException;
import com.daom.exception.NotStudentException;
import com.daom.service.MemberService;
import com.daom.service.ResponseService;
import com.daom.service.StudentService;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

@RestController
@RequiredArgsConstructor
@RequestMapping("/student")
public class StudentController {
    private final StudentService studentService;
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
    @PostMapping(value = "/profile/upload")//프로핊사진업로드
    public RestResponse fileUpload(@AuthenticationPrincipal UserDetailsImpl userDetails, @RequestPart("thumbnail") MultipartFile thumbnail) {
        Student student = userDetails.getMember().getStudent();
        if (student == null) {
            throw new NotStudentException();
        }
        studentService.profileUpload(student.getId(), thumbnail);
        //file 테이블에 저장한 사진정보가 db에 저장되어야한다

        return responseService.getSuccessResponse();
    }

    @PostMapping(value = "/profile/delete")//프로필사진삭제
    public RestResponse profileDelete(@AuthenticationPrincipal UserDetailsImpl userDetails) {
        Student student = userDetails.getMember().getStudent();
        if (student == null) {
            throw new NotStudentException();
        }
        studentService.profileDelete(student.getId());
        return responseService.getSuccessResponse();
    }

}
