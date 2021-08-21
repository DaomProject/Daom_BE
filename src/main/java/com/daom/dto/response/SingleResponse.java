package com.daom.dto.response;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
public class SingleResponse<T> extends RestResponse{
    private T data;
}
