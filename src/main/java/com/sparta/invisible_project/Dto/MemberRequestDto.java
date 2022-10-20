package com.sparta.invisible_project.Dto;

import lombok.Getter;
import lombok.NoArgsConstructor;

import javax.validation.constraints.NotBlank;

@Getter
@NoArgsConstructor
public class MemberRequestDto {
    @NotBlank
    private String username;
    @NotBlank
    private String password;

    public MemberRequestDto(String username, String password) {
        this.username = username;
        this.password = password;
    }
    public void setPasswordEncoder(String encodedPassword){
        this.password = encodedPassword;
    }
}
