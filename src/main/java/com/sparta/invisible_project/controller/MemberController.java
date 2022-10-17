package com.sparta.invisible_project.controller;

import com.sparta.invisible_project.dto.*;
import com.sparta.invisible_project.service.MemberService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;

@RestController
@RequestMapping("/api")
@RequiredArgsConstructor
public class MemberController {

    private final MemberService memberService;

    // 회원 가입
    @PostMapping("/signup")
    public ResponseDto<?> signup(@RequestBody @Valid SignupReqDto signupReqDto) {
        return memberService.createAccount(signupReqDto);
    }

    // 로그인
    @PostMapping("/login")
    public ResponseEntity<?> login(@RequestBody @Valid LoginReqDto loginReqDto) {
        return memberService.loginAccount(loginReqDto);
    }

    // 토큰 재발급
    @PostMapping("/reissue")
    public ResponseEntity<TokenDto> reissue(@RequestBody TokenRequestDto tokenRequestDto) {
        System.out.println(tokenRequestDto);
        return ResponseEntity.ok(memberService.reissue(tokenRequestDto));
    }

}
