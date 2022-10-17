package com.sparta.invisible_project.Service;

import com.sparta.invisible_project.repository.MemberRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class MemberService {
    private final MemberRepository memberRepository;
    //여긴 뭔가 연태님 로그인 펑션이라..상의후 결정..

}
