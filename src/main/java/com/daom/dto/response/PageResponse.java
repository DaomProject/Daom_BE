package com.daom.dto.response;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class PageResponse<T> extends RestResponse{
    private int totalCount;
    private int nowCount;
    private int page;
    private T data;
}
