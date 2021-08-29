package com.daom.controller;

import com.daom.advice.ExceptionAdvice;
import com.daom.config.jwt.JwtTokenProvider;
import com.daom.dto.MemberJoinDto;
import com.daom.dto.StudentJoinDto;
import com.daom.exception.UsernameDuplicationException;
import com.daom.service.MemberService;
import com.daom.service.ResponseService;
import com.google.gson.Gson;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.ResultActions;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;
import org.springframework.test.web.servlet.result.MockMvcResultMatchers;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;

@ExtendWith(MockitoExtension.class)
class AuthControllerUnitTest {
    @InjectMocks
    private AuthController authController;

    @Mock
    private ResponseService responseService;
    @Mock
    private MemberService memberService;

    private MockMvc mockMvc;

    @BeforeEach
    public void init() {
        mockMvc = MockMvcBuilders.standaloneSetup(authController)
                .setControllerAdvice(new ExceptionAdvice(responseService)).build();
    }

    @DisplayName("[학생] 회원가입 성공")
    @Test
    void signUpStudentSuccess() throws Exception {
        //given
        final StudentJoinDto studentJoinDto = makeStudentJoinDto();
        Mockito.doReturn(1L).when(memberService).saveStudent(studentJoinDto);

        //when
        final ResultActions resultActions = mockMvc.perform(
                MockMvcRequestBuilders.post("/auth/join/student")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(new Gson().toJson(studentJoinDto))
        );

        //then
        resultActions.andExpect(MockMvcResultMatchers.status().isOk()).andReturn();

    }

    @DisplayName("[학생] 아이디 중복으로 인한 회원가입 실패")
    @Test()
    public void signUpStudentFail() throws Exception {
        //given
        final StudentJoinDto studentJoinDto = makeStudentJoinDto();
        Mockito.doThrow(new UsernameDuplicationException()).when(memberService).saveStudent(makeStudentJoinDto());

        //when
        final ResultActions resultActions = mockMvc.perform(
                MockMvcRequestBuilders.post("/auth/join/student")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(new Gson().toJson(studentJoinDto))
        );

        //then
        resultActions.andExpect(MockMvcResultMatchers.status().isBadRequest());
    }

    @DisplayName("[업체] 회원가입 성공")
    @Test
    void signUpShopSuccess() throws Exception {
        //given
        final MemberJoinDto memberJoinDto = makeShopJoinDto();
        Mockito.doReturn(1L).when(memberService).saveShop(memberJoinDto);

        //when
        final ResultActions resultActions = mockMvc.perform(
                MockMvcRequestBuilders.post("/auth/join/shop")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(new Gson().toJson(memberJoinDto))
        );

        //then
        resultActions.andExpect(MockMvcResultMatchers.status().isOk()).andReturn();

    }

    @DisplayName("[업체] 아이디 중복으로 인한 회원가입 실패")
    @Test()
    public void signUpShopFail() throws Exception {
        //given
        final MemberJoinDto memberJoinDto = makeShopJoinDto();
        Mockito.doThrow(new UsernameDuplicationException()).when(memberService).saveShop(memberJoinDto);

        //when
        final ResultActions resultActions = mockMvc.perform(
                MockMvcRequestBuilders.post("/auth/join/shop")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(new Gson().toJson(memberJoinDto))
        );

        //then
        resultActions.andExpect(MockMvcResultMatchers.status().isBadRequest());
    }

    private StudentJoinDto makeStudentJoinDto(){
        return StudentJoinDto.builder()
                .username("test")
                .password("test1234")
                .admissionYear(2016L)
                .univName("한국대학교")
                .nickname("테스터")
                .build();
    }

    private MemberJoinDto makeShopJoinDto(){
        return MemberJoinDto.builder()
                .username("test2")
                .password("test1234")
                .nickname("테스터2")
                .build();
    }
}