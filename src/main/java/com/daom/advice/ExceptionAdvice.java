package com.daom.advice;

import com.daom.dto.response.RestResponse;
import com.daom.exception.*;
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

    // 1000번대 에러 : 멤버 관련 에러
    @ExceptionHandler(NoSuchMemberException.class)
    @ResponseStatus(HttpStatus.BAD_REQUEST)
    public RestResponse noSuchMemberException(){
        return responseService.getFailResponse(-1000, "해당 정보와 일치하는 멤버가 없습니다.");
    }

    @ExceptionHandler(UsernameDuplicationException.class)
    @ResponseStatus(HttpStatus.BAD_REQUEST)
    public RestResponse usernameDuplicationException(){
        return responseService.getFailResponse(-1001, "중복된 회원 아이디입니다.");
    }

    @ExceptionHandler(NicknameDuplicationException.class)
    @ResponseStatus(HttpStatus.BAD_REQUEST)
    public RestResponse nicknameDuplicationException(){
        return responseService.getFailResponse(-1002, "중복된 회원 닉네임입니다.");
    }

    @ExceptionHandler(UnivNameNotFoundException.class)
    @ResponseStatus(HttpStatus.BAD_REQUEST)
    public RestResponse univNameNotFoundException(){
        return responseService.getFailResponse(-1003, "해당 이름으로 등록된 대학교 이름이 없습니다.");
    }

    @ExceptionHandler(NoSuchCategoryException.class)
    @ResponseStatus(HttpStatus.BAD_REQUEST)
    public RestResponse noSuchCategoryException(){
        return responseService.getFailResponse(-1004, "해당 카테고리는 존재하지 않습니다.");
    }

    @ExceptionHandler(MenuIndexAndFileNotMatchException.class)
    @ResponseStatus(HttpStatus.BAD_REQUEST)
    public RestResponse menuIndexAndFileNotMatchException(){
        return responseService.getFailResponse(-1005, "메뉴파일을 삽입한 메뉴의 순서를 알려주세요.");
    }

    @ExceptionHandler(NoSuchShopException.class)
    @ResponseStatus(HttpStatus.BAD_REQUEST)
    public RestResponse noSuchShopException(){
        return responseService.getFailResponse(-1006, "해당 Shop을 찾을 수 없습니다.");
    }

    @ExceptionHandler(NoSuchShopFileException.class)
    @ResponseStatus(HttpStatus.BAD_REQUEST)
    public RestResponse noSuchShopFileException(){
        return responseService.getFailResponse(-1007, "해당 ShopFile을 찾는데 실패했습니다.");
    }

    @ExceptionHandler(NotAuthorityThisJobException.class)
    @ResponseStatus(HttpStatus.BAD_REQUEST)
    public RestResponse notAuthorityThisJobException(){
        return responseService.getFailResponse(-1008, "해당 작업을 할 권한이 없습니다.");
    }

    @ExceptionHandler(UnmatchPasswordException.class)
    @ResponseStatus(HttpStatus.BAD_REQUEST)
    public RestResponse unmatchPasswordException(){
        return responseService.getFailResponse(-1009, "비밀번호가 틀렸습니다.");
    }

    @ExceptionHandler(FindShopXYException.class)
    @ResponseStatus(HttpStatus.BAD_REQUEST)
    public RestResponse findShopXYException(){
        return responseService.getFailResponse(-1010, "좌표를 찾는데 실패했습니다. ( 정확한 주소를 입력하세요 )");
    }

    @ExceptionHandler(NoSuchReviewException.class)
    @ResponseStatus(HttpStatus.INTERNAL_SERVER_ERROR)
    public RestResponse noSuchReviewException(){
        return responseService.getFailResponse(-1011, "해당 리뷰를 찾을 수 없습니다.");
    }

    @ExceptionHandler(NoSuchZzimException.class)
    @ResponseStatus(HttpStatus.INTERNAL_SERVER_ERROR)
    public RestResponse noSuchZzimException(){
        return responseService.getFailResponse(-1012, "해당 찜 항목을 찾을 수 없습니다.");
    }

    @ExceptionHandler(NoSuchLikeException.class)
    @ResponseStatus(HttpStatus.INTERNAL_SERVER_ERROR)
    public RestResponse noSuchLikeException(){
        return responseService.getFailResponse(-1013, "해당 좋아요 항목을 찾을 수 없습니다.");
    }

    @ExceptionHandler(NotInsertSortedMethodException.class)
    @ResponseStatus(HttpStatus.BAD_REQUEST)
    public RestResponse notInsertSortedMethodException(){
        return responseService.getFailResponse(-1014, "정해지지않은 정렬방법입니다.");
    }

    @ExceptionHandler(FileStoreException.class)
    @ResponseStatus(HttpStatus.INTERNAL_SERVER_ERROR)
    public RestResponse fileStoreException(){
        return responseService.getFailResponse(-2000, "파일을 저장하는데 실패했습니다.");
    }

    @ExceptionHandler(NaveMapApiException.class)
    @ResponseStatus(HttpStatus.INTERNAL_SERVER_ERROR)
    public RestResponse naveMapApiException(Exception e){
        e.printStackTrace();
        return responseService.getFailResponse(-2001, "좌표 검색 API 호출이 실패하였습니다.");
    }

    @ExceptionHandler(MessageApiException.class)
    @ResponseStatus(HttpStatus.INTERNAL_SERVER_ERROR)
    public RestResponse messagingApiException(Exception e){
        e.printStackTrace();
        return responseService.getFailResponse(-2002, "메일 시스템에 오류가 발생하였습니다.");
    }
}
