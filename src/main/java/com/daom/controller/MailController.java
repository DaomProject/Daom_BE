package com.daom.controller;

import com.daom.dto.MailDto;
import com.daom.dto.response.RestResponse;
import com.daom.exception.MessageApiException;
import com.daom.service.MailService;
import com.daom.service.MemberService;
import com.daom.service.ResponseService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.MimeMessageHelper;
import org.springframework.web.bind.annotation.*;

import javax.mail.MessagingException;
import javax.mail.internet.InternetAddress;
import javax.mail.internet.MimeMessage;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpSession;
import javax.websocket.SessionException;
import java.util.UUID;
import java.util.concurrent.TimeoutException;

@RestController
@RequiredArgsConstructor
@Slf4j
@RequestMapping("/mail")
public class MailController {
    private final ResponseService responseService;
    private static final String AUTH_NUM = "AUTH_NUM";
    private final MailService mailService;
    private final MemberService memberService;

    @PostMapping("/send/register")
    public RestResponse sendMail(@RequestBody MailDto mailDto, HttpServletRequest request) {

        HttpSession authSession = request.getSession();
        String authKey = UUID.randomUUID().toString();
        authKey = authKey.substring(0, 8);
        authSession.setAttribute(AUTH_NUM, authKey);
        authSession.setMaxInactiveInterval(180);// 세션 유지 시간 : 180초
        try {
            mailService.sendMail(mailDto.getMail(), "[다옴] 회원가입 인증번호", "다옴 회원가입 인증번호 : " + authKey);
        } catch (MessagingException messagingException) {
            throw new MessageApiException();
        }

        return responseService.getSuccessResponse();
    }

    // 반드시 메일인증 이후에 사용해야한다.
    @PostMapping("/send/temp-password")
    public RestResponse sendTempPassword(@RequestBody MailDto mailDto){
        String tempPassword = memberService.setTempPassword(mailDto);
        try {
            mailService.sendMail(mailDto.getMail(), "[다옴] 임시비밀번호 설정", "다옴 임시 비밀번호 : " + tempPassword);
        } catch (MessagingException messagingException) {
            throw new MessageApiException();
        }

        return responseService.getSuccessResponse();
    }

    @GetMapping("/check")
    public RestResponse checkAuth(@RequestParam(value = "auth") String myAuthKey,
                                  @SessionAttribute(name = AUTH_NUM, required = false) String authKey) {
        if (authKey == null) {
            return responseService.getFailResponse(-0, "인증번호 세션이 만료되었습니다.");
        }
        if (authKey.equals(myAuthKey)) {
            return responseService.getSuccessResponse();
        } else {
            return responseService.getFailResponse(-0, "인증번호가 일치하지 않습니다.");
        }
    }
}
