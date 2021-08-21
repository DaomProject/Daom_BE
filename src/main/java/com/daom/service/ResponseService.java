package com.daom.service;

import com.daom.dto.response.RestResponse;
import com.daom.dto.response.SingleResponse;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class ResponseService {
    public <T> SingleResponse<T> getSingleResponse(T data) {
        SingleResponse<T> result = new SingleResponse<>();
        result.setData(data);
        result.setSuccessResult();
        return result;
    }

    public RestResponse getSuccessResponse(){
        RestResponse result = new RestResponse();
        result.setSuccessResult();
        return result;
    }

    public RestResponse getFailResponse(int code, String msg){
        RestResponse result = new RestResponse();
        result.setCode(code);
        result.setMsg(msg);
        result.setSuccess(false);
        return result;
    }

}
