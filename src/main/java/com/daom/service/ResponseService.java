package com.daom.service;

import com.daom.dto.response.PageResponse;
import com.daom.dto.response.RestResponse;
import com.daom.dto.response.SingleResponse;
import org.springframework.stereotype.Service;

@Service
public class ResponseService {
    public <T> SingleResponse<T> getSingleResponse(T data) {
        SingleResponse<T> result = new SingleResponse<>();
        result.setData(data);
        result.setSuccessResult();
        return result;
    }

    public <T> PageResponse<T> getPageResponse(T data, int count){
        PageResponse<T> result = new PageResponse<>();
        result.setData(data);
        result.setCount(count);
        result.setSuccessResult();
        return result;
    }

    public RestResponse getSuccessResponse(){
        RestResponse result = new RestResponse();
        result.setSuccessResult();
        return result;
    }

    public RestResponse getSuccessResponse(String message){
        RestResponse result = new RestResponse();
        result.setSuccessResult();
        result.setMsg(message);
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
