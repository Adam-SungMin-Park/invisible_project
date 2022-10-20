package com.sparta.invisible_project.Dto;

import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public class ResponseDto<T> {
    private T data;
    private Boolean success;
    private Error error;

    public static <T> ResponseDto<T> success(T data){
        return new ResponseDto<>(data,true,null);
    }



}
