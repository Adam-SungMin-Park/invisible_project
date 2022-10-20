package com.sparta.invisible_project.Jwt.filter;

import com.sparta.invisible_project.Jwt.JwtUtil;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.filter.OncePerRequestFilter;

import javax.servlet.FilterChain;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;

@Slf4j
@RequiredArgsConstructor
public class JwtAuthFilter extends OncePerRequestFilter {

    private final JwtUtil jwtUtil;

    @Override
    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain filterChain) throws ServletException, IOException {
        String accessToken = jwtUtil.getHeaderToken(request, "Access");
        String refreshToken = jwtUtil.getHeaderToken(request, "Refresh");

        if (accessToken != null) {
            if (!jwtUtil.tokenValidation(accessToken)) {
                System.out.println("JwtAuthFilter.doFilterInternal");
                return;
            }
            setAuthentication(jwtUtil.getUsernameFromToken(accessToken));
        }else if(refreshToken !=null){
            if (!jwtUtil.refreshTokenValidation(refreshToken)) {
                System.out.println("JwtAuthFilter.doFilterInternal");
                return;
            }
            setAuthentication(jwtUtil.getUsernameFromToken(refreshToken));

        }
        filterChain.doFilter(request, response);
    }
    public void setAuthentication(String username){
        Authentication authentication = jwtUtil.createAuthentication(username);
        SecurityContextHolder.getContext().setAuthentication(authentication);
    }
}
