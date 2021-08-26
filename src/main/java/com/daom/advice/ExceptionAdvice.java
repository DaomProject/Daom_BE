package com.daom.advice;

import com.daom.dto.response.RestResponse;
import com.daom.exception.NoSuchUserException;
import com.daom.exception.UsernameDuplicationException;
import com.daom.service.ResponseService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestControllerAdvice;

@RequiredArgsConstructor
@RestControllerAdvice
public class ExceptionAdvice {
    private final ResponseService responseService;

    @ExceptionHandler(NoSuchUserException.class)
    @ResponseStatus(HttpStatus.BAD_REQUEST)
    public RestResponse noSuchUserException(){
        return responseService.getFailResponse(-1000, "해당 정보와 일치하는 멤버가 없습니다.");
    }

    @ExceptionHandler(UsernameDuplicationException.class)
    @ResponseStatus(HttpStatus.BAD_REQUEST)
    public RestResponse usernameDuplicationException(){
        return responseService.getFailResponse(-1001, "중복된 회원 아이디입니다.");
    }
}
