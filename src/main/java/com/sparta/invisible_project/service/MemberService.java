package com.sparta.invisible_project.service;

import com.sparta.invisible_project.dto.*;
import com.sparta.invisible_project.model.Authority;
import com.sparta.invisible_project.model.Member;
import com.sparta.invisible_project.model.RefreshToken;
import com.sparta.invisible_project.repository.MemberRepository;
import com.sparta.invisible_project.repository.RefreshTokenRepository;
import com.sparta.invisible_project.security.JwtFilter;
import com.sparta.invisible_project.security.MemberDetails;
import com.sparta.invisible_project.security.TokenProvider;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.config.annotation.authentication.builders.AuthenticationManagerBuilder;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import javax.transaction.Transactional;

@Service
@RequiredArgsConstructor
public class MemberService implements UserDetailsService {

    private final AuthenticationManagerBuilder authenticationManagerBuilder;
    private final MemberRepository memberRepository;
    private final PasswordEncoder passwordEncoder;
    private final TokenProvider tokenProvider;
    private final RefreshTokenRepository refreshTokenRepository;

    @Override
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
        Member member = memberRepository
                .findByUsername(username)
                .orElseThrow(
                        ()->new UsernameNotFoundException(username+"을 찾을 수 없습니다")
                );
        return new MemberDetails(member);
    }

    // 회원가입
    @Transactional
    public ResponseDto<?> createAccount(SignupReqDto signupReqDto) {
        String username = signupReqDto.getUsername();
        String password = signupReqDto.getPassword();
        String passwordConfirm = signupReqDto.getPasswordConfirm();
        if(memberRepository.existsByUsername(username)) {
            throw new RuntimeException("이미 가입된 유저입니다");
        }
        if(!password.equals(passwordConfirm)) {
            throw new RuntimeException("비밀번호와 비밀번호 확인이 일치하지 않습니다");
        }
        Member member = new Member(username, passwordEncoder.encode(password), Authority.ROLE_USER);
        return ResponseDto.success(memberRepository.save(member));
    }

    // 로그인
    @Transactional
    public ResponseEntity<?> loginAccount(LoginReqDto loginReqDto) {

        // 받아온 걸로 Security 인증용 토큰 생성
        UsernamePasswordAuthenticationToken authenticationToken = loginReqDto.toAuthentication();

        // 검증
        // (Security Depth : SecurityContextHolder > Context > Authentication > UsernamePasswordAuthenticationToken > MemberDetails)
        Authentication authentication = authenticationManagerBuilder.getObject().authenticate(authenticationToken);

        Member member = memberRepository.findByUsername(loginReqDto.getUsername()).orElse(null);
        // 인증 정보를 기반으로 JWT 토큰 생성
        TokenDto tokenDto = tokenProvider.generateTokenDto(authentication);
        
        // refresh 토큰 db 저장
        RefreshToken refreshToken = RefreshToken.builder()
                // key 검증된 유저 이름
                .key(authentication.getName())
                // value 문자열로 된 리프레쉬 토큰
                .value(tokenDto.getRefreshToken())
                .build();
        refreshTokenRepository.save(refreshToken);
        
        // 클라이언트 발급용 토큰 헤더에 넣는 작업
        HttpHeaders httpHeaders = new HttpHeaders();
        // 규칙인 Authorization 필드 만들고 Jwt 토큰이니까 value에 Bearer 붙여줌 그리고 위에서 만든 토큰들 붙임
        httpHeaders.add(JwtFilter.AUTHORIZATION_HEADER, JwtFilter.BEARER_PREFIX + tokenDto.getAccessToken());
        httpHeaders.add("Refresh-Token", tokenDto.getRefreshToken());

        // 토큰 발급
        return new ResponseEntity<>(ResponseDto.success(member), httpHeaders, HttpStatus.OK);
    }

    // 토큰 재발급
    @Transactional
    public TokenDto reissue(TokenRequestDto tokenRequestDto) {
        // refresh token 확인
        if (!tokenProvider.validateToken(tokenRequestDto.getRefreshToken())) {
            throw new RuntimeException(("Refresh Token이 유효하지 않습니다"));
        }

        // accesss token에서 member id 가져오기
        Authentication authentication = tokenProvider.getAuthentication(tokenRequestDto.getAccessToken());

        // 저장소에서 member id를 기반으로 refresh token 값 가져옴. UsernamePasswordAuthenticationToken.getName(Principal.getName())
        RefreshToken refreshToken = refreshTokenRepository.findByKey(authentication.getName())
                .orElseThrow(()->new RuntimeException("로그아웃 된 사용자입니다"));

        // refresh token 검사
        if (!refreshToken.getValue().equals(tokenRequestDto.getRefreshToken())) {
            throw new RuntimeException("토큰의 유저 정보가 일치하지 않습니다");
        }

        // 토큰 생성
        TokenDto tokenDto = tokenProvider.generateTokenDto(authentication);

        // 새로 발급한 refresh 토큰 정보 db 업데이트
        RefreshToken refreshRefreshToken = refreshToken.updateValue(tokenDto.getRefreshToken());
        refreshTokenRepository.save(refreshRefreshToken);

        // 새로 토큰 발급했으니 클라이언트한테 돌려준다
        return tokenDto;
    }
}
