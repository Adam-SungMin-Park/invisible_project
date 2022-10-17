package com.sparta.invisible_project.dto;

import lombok.Getter;
import lombok.Setter;
import javax.validation.constraints.NotBlank;

@Getter
@Setter
public class SignupReqDto {

    @NotBlank(message = "이름을 입력해주세요")
    private String username;

    @NotBlank(message = "비밀번호를 입력해주세요")
    private String password;

    private String passwordConfirm;
}
