package com.sparta.invisible_project.Controller;

import com.sparta.invisible_project.Service.MemberService;
import lombok.AllArgsConstructor;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Required;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api")
@RequiredArgsConstructor
public class MemberController {
    private final MemberService memberService;

    //return type 상의후 결정.
    //여긴 뭔가 연태님 로그인 펑션이라..상의후 결정..


}
