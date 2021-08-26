package com.daom.dto.response;

import lombok.*;

@Getter
@Setter
public class SingleResponse<T> extends RestResponse{
    private T data;
}
