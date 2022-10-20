package com.sparta.invisible_project.security;

import lombok.RequiredArgsConstructor;
import org.springframework.boot.autoconfigure.condition.ConditionalOnWebApplication;
import org.springframework.boot.autoconfigure.security.ConditionalOnDefaultWebSecurity;
import org.springframework.boot.autoconfigure.security.SecurityProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.annotation.Order;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configuration.WebSecurityCustomizer;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;

@EnableWebSecurity
@RequiredArgsConstructor
@Configuration
@ConditionalOnDefaultWebSecurity
@ConditionalOnWebApplication(type = ConditionalOnWebApplication.Type.SERVLET)
public class WebSecurityConfig {
    private final TokenProvider tokenProvider;
    private final JwtAuthenticationEntryPoint jwtAuthenticationEntryPoint;
    private final JwtAccessDeniedHandler jwtAccessDeniedHandler;

    //  DB 비밀번호 암호화용
    @Bean
    public PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }

    //  h2 database 접속용
    @Bean
    public WebSecurityCustomizer webSecurityCustomizer() {
        return (web)->web.ignoring()
                .antMatchers("/h2-console/**", "/favicon.ico");
    }

    @Bean
    @Order(SecurityProperties.BASIC_AUTH_ORDER)
    public SecurityFilterChain filterChain(HttpSecurity http) throws Exception {
        // CSRF
        http.csrf().disable()

                // 예외처리 커스텀 클레스 추가
                .exceptionHandling()
                // 익셉션 제어
                .authenticationEntryPoint(jwtAuthenticationEntryPoint)
                // 유효한 자격증명을 제공하지 않고 접근하려 할때 401
                .accessDeniedHandler(jwtAccessDeniedHandler)
                // 필요한 권한이 없이 접근하려 할때 403

                // h2-console 설정 추가
                .and()
                .headers()
                // 헤더에 xFrame 옵션 허용부
                .frameOptions()
                .sameOrigin()
                /*
                 * X-Frame-Options 헤더 설정방법
                 * 웹 어플리케이션에 HTML 삽입 취약점이 존재하면 공격자는 다른 서버에 위치한 페이지를 <frame>, <iframe>, <object> 등으로 삽입하여 다양한 공격에 사용할 수 있다.
                 * 피해자의 입장에서는 링크를 눌렀을 때 의도했던 것과는 다른 동작을 하게 한다하여 이를 클릭재킹(Clickjacking)이라 부른다.
                 * 웹 페이지를 공격에 필요한 형태로 조작하기 때문에 "사용자 인터페이스 덧씌우기"(User Interface redress) 공격이라고도 부른다.
                 * (XSS 공격을 생각하면 됨)
                 * 이런 공격을 다른 웹 브라우저가 일부 해결해줄 수 있는 방안이 "X-Frame-Options:" 헤더이다.
                 * 2009년에 MS가 IE8에 도입한 이후로 대부분의 웹 브라우저가 채택하고 있다.
                 * 이 헤더의 값은 "DENY", "SAMEORGIN", "ALLOW-FROM origin"을 가질 수 있다.
                 *
                 * DENY: "이 홈페이지는 다른 홈페이지에서 표시할 수 않음"
                 * SAMEORIGIN: "이 홈페이지는 동일한 도메인의 페이지 내에서만 표시할 수 있음"
                 * ALLOW-FROM origin: "이 홈페이지는 origin 도메인의 페이지에서 표함하는 것을 허용함"
                 */

                // jwt 쓸거니까 시큐리티 세션 방식 꺼야함
                .and()
                .sessionManagement()
                .sessionCreationPolicy(SessionCreationPolicy.STATELESS)
                // Session 정책 중 STATELESS는 서버에서 어떠한 유저 정보도 저장하지 않겠다는 뜻이므로
                // 세션 방식을 사용하지 않겠다는 말이 됨

                // 토큰 없을 때 들어오는 요청 허가
                .and()
                .authorizeRequests()
                .antMatchers("/api/auth/**")
                .authenticated()
                .anyRequest()
                .permitAll()
                // /api/auth/**로 오는 권한 인가 요청만 제외 확인, 인증처리, 그 외에는 무슨 요청이 오든 permitAll.

                //  jwtFilter를 jwt 커스텀 설정을 만들어 UsernamePasswordAuthenticationFilter 보다 앞으로 오게 배치
                .and()
                .apply(new JwtSecurityConfig(tokenProvider));
                // 여기서 addFilterBefore 안하고 jwtConfig 커스텀 클래스에서 설정함

        return http.build();

    }

}
