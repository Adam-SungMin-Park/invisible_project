package com.sparta.invisible_project.security;

import lombok.RequiredArgsConstructor;
import org.springframework.security.config.annotation.SecurityConfigurerAdapter;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.web.DefaultSecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;

// 여기서 jwt 토큰 발급,확인,재발급 필터를 security 인증인가 시스템 보다 앞으로 빼줌
// Security + JWT 필수 연동 2가지 중 하나
// (JWT 필터 최전선 배치 + Security 인가시스템에 Token 유저 정보 등록)
@RequiredArgsConstructor
public class JwtSecurityConfig extends SecurityConfigurerAdapter <DefaultSecurityFilterChain, HttpSecurity> {
    // SecurityConfigurerAdapter는 원하는 메소드만 구현해 사용할 수 있도록 스프링 시큐리티가 준비해둔 SecurityConfigurer의
    // 상속용 기본 클레스임. SecurityConfigurer를 interface로 implement하고 있음.
    // 제너릭 1번 인자 : 제너릭 2번이 빌드 중인 객체, 제너릭 2번 인자 : 1번을 빌드하고 다음으로 구성된 빌더
    // 몬가 커스텀 그래픽 설정 옵션 같은 느낌의 녀석임

    // 만든 토큰 서비스 객체 필터에 넣어서 시큐리티보다 앞단으로 빼줘야하니까 만듬
    private final TokenProvider tokenProvider;

    @Override
    public void configure(HttpSecurity builder) throws Exception {
        // 토큰 서비스 넣어서 필터 만들기
        JwtFilter customFilter = new JwtFilter(tokenProvider);
        // 설정할 걸 HttpSecurity 참조타입 빌더로 구현해주면 DefaultSecurityFilterChain 설정에 합류시켜줌
        // addFilterBefore 1번 인자를 2번 인자 앞에 배치
        builder.addFilterBefore(customFilter, UsernamePasswordAuthenticationFilter.class);
        
        // 이게 제대로 됬으면 security가 인가된 사용자를 관리 or 저장해두는 SecurityContextHolder에 사용자를 넣어줘야 컨트롤러까지 갈 수 있음
    }
}