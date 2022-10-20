package com.sparta.invisible_project.Dto;

import lombok.Getter;
import lombok.NoArgsConstructor;

import javax.validation.constraints.NotBlank;

@Getter
@NoArgsConstructor
public class LoginDto {
    @NotBlank
    private String username;

    @NotBlank
    private String password;

    public LoginDto(String username, String password) {
        this.username = username;
        this.password = password;
    }
}
