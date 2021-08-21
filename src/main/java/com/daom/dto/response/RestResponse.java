package com.daom.dto.response;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class RestResponse{
    private boolean success;
    private int code;
    private String msg;


    public void setSuccessResult(){
        this.success = true;
        this.code = 200;
        this.msg = "success";
    }
}