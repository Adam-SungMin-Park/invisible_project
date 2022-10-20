package com.sparta.invisible_project.Controller;


import com.sparta.invisible_project.Dto.LoginDto;
import com.sparta.invisible_project.Dto.MemberRequestDto;
import com.sparta.invisible_project.Dto.LoginResponseDto;
import com.sparta.invisible_project.Dto.ResponseDto;
import com.sparta.invisible_project.Jwt.JwtUtil;
import com.sparta.invisible_project.Service.MemberService;
import com.sparta.invisible_project.Service.UserDetailsImpl;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpServletResponse;
import javax.validation.Valid;

@RestController
@RequestMapping("/api")
@RequiredArgsConstructor
public class MemberController {

    private final MemberService memberService;
    private final JwtUtil jwtUtil;

    @PostMapping("/member/signup")
    public LoginResponseDto signup(@RequestBody @Valid MemberRequestDto requestDto) {
        return memberService.signup(requestDto);
    }

    @PostMapping("/member/login")
    public LoginResponseDto login(@RequestBody @Valid LoginDto dto, HttpServletResponse response) {
        return memberService.login(dto, response);
    }

    @GetMapping("/issue/token")
    public LoginResponseDto issuedToken(@AuthenticationPrincipal UserDetailsImpl userDetails, HttpServletResponse response){
        response.addHeader(JwtUtil.ACCESS_TOKEN,jwtUtil.createToken(userDetails.getMember().getUsername(),"Access"));
        return new LoginResponseDto("Issue Success", HttpStatus.OK.value());
    }
}
