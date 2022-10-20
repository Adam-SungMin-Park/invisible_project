package com.sparta.invisible_project.Service;

import com.sparta.invisible_project.Dto.LoginDto;
import com.sparta.invisible_project.Dto.TokenDto;
import com.sparta.invisible_project.Entity.Member;
import com.sparta.invisible_project.Dto.MemberRequestDto;
import com.sparta.invisible_project.Dto.LoginResponseDto;
import com.sparta.invisible_project.Entity.RefreshToken;
import com.sparta.invisible_project.Jwt.JwtUtil;
import com.sparta.invisible_project.Repository.MemberRepository;
import com.sparta.invisible_project.Repository.RefreshTokenRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import javax.servlet.http.HttpServletResponse;
import javax.transaction.Transactional;
import java.util.Optional;

@Service
@RequiredArgsConstructor
public class MemberService {
    private final MemberRepository memberRepository;
    private final JwtUtil jwtUtil;

    private final RefreshTokenRepository refreshTokenRepository;
    private final PasswordEncoder passwordEncoder;

    @Transactional
    public LoginResponseDto signup(MemberRequestDto requestDto) {
        //username duplication check
        if (memberRepository.findByUsername(requestDto.getUsername()).isPresent()) {
            throw new RuntimeException("duplication in username");
        };

        requestDto.setPasswordEncoder(passwordEncoder.encode(requestDto.getPassword()));
        Member member = new Member(requestDto);

        memberRepository.save(member);
        return new LoginResponseDto("Sign up Success", HttpStatus.OK.value());

    }

    public LoginResponseDto login(LoginDto loginDto, HttpServletResponse response) {
        System.out.println(loginDto.getUsername());
        Member member = memberRepository.findByUsername(loginDto.getUsername()).orElseThrow(
                () -> new RuntimeException("User not found")
        );
        if (!passwordEncoder.matches(loginDto.getPassword(), member.getPassword())) {
            throw new RuntimeException("Password mismatch");
        }
        TokenDto tokenDto = jwtUtil.createAllToken(loginDto.getUsername());
        Optional<RefreshToken> refreshToken = refreshTokenRepository.findByMemberUsername(loginDto.getUsername());
        if (refreshToken.isPresent()) {
            refreshTokenRepository.save(refreshToken.get().update(tokenDto.getRefreshToken()));
        } else {
            RefreshToken newRefreshToken = new RefreshToken(tokenDto.getRefreshToken(), loginDto.getUsername());
            refreshTokenRepository.save(newRefreshToken);
        }
        setHeader(response, tokenDto);

        return new LoginResponseDto("Login Success", HttpStatus.OK.value());
    }
    private void setHeader(HttpServletResponse response, TokenDto tokenDto){
        response.addHeader(JwtUtil.ACCESS_TOKEN, tokenDto.getAccessToken());
        response.addHeader(JwtUtil.REFRESH_TOKEN, tokenDto.getRefreshToken());
    }
}
