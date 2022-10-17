package com.sparta.invisible_project.security;

import com.sparta.invisible_project.dto.TokenDto;
import com.sparta.invisible_project.model.Member;
import com.sparta.invisible_project.repository.MemberRepository;
import io.jsonwebtoken.*;
import io.jsonwebtoken.io.Decoders;
import io.jsonwebtoken.security.Keys;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Component;

import java.security.Key;
import java.util.Date;
import java.util.stream.Collectors;

@Slf4j
@Component
public class TokenProvider {

    // JWT 제공자 (JWT에 대한 모든 개념은 여기서 시작하고 끝날거임)

    /**
     * JWT (Json Web Token) - header(json) + payload(json) + signature
     * <p>
     * JWS (Json Web Signature) - 서버에서 인증을 근거로 인증정보를 서버의 private key로 서명 한것을 토큰화 한것
     * <p>
     * JWE (Json Web Encryption) - 서버와 클라이언트 간 암호화된 데이터를 토큰화 한 것
     * <p>
     * JWK (Json Web Key) - JWE 에서 payload encryption에 쓰인 키를 토큰화 한 것
     * <p>
     * <p>
     * JWT구조
     * <p>
     * xxxxx.yyyyy.zzzzz == HEADER.PAYLOAD.SIGNATURE
     * <p>
     * HEADER : 시그니처를 해싱하기 위한 알고리즘 정보가 담겨져 있는 부분
     * <p>
     * 예) {
     *   "alg": "HS256",
     *   "typ": "JWT"
     * }
     * <p>
     * PAYLOAD : claim을 담고있는 부분
     * <p>
     * 예) {
     *   "sub": "1234567890",
     *   "name": "John Doe",
     *   "admin": true
     * }
     * <p>
     * SIGNATURE : 서명은 토큰이 변조되지 않았음을 증명하는 무결성을 위해 사용된다. 시그니쳐는 인코딩된 헤더, 페이로드, 비밀키, 헤더에 정의된 서명 알고리즘을 이용하여 생성
     * <p>
     * 예) HMACSHA256(
     *   base64UrlEncode(header) + "." +
     *   base64UrlEncode(payload),
     *   secret)
     * <p>
     * <p>
     * PAYLOAD의 등록된 (registered) 클레임 - 필수는 아니지만 유용하고 상호 운용 가능한 클레임 집합을 제공하기 위해 권장되는 미리 정의된 클레임 집합
     * <p>
     * iss: 토큰 발급자 (issuer)
     * <p>
     * sub: 토큰 제목 (subject)
     * <p>
     * aud: 토큰 대상자 (audience)
     * <p>
     * exp: 토큰의 만료시간 (expiraton), 시간은 NumericDate 형식으로 되어있어야 하며 (예: 1480849147370) 언제나 현재 시간보다 이후로 설정되어있어야합니다.
     * <p>
     * nbf: Not Before 를 의미하며, 토큰의 활성 날짜와 비슷한 개념입니다. 여기에도 NumericDate 형식으로 날짜를 지정하며, 이 날짜가 지나기 전까지는 토큰이 처리되지 않습니다.
     * <p>
     * iat: 토큰이 발급된 시간 (issued at), 이 값을 사용하여 토큰의 age 가 얼마나 되었는지 판단 할 수 있습니다.
     * <p>
     * jti: JWT의 고유 식별자로서, 주로 중복적인 처리를 방지하기 위하여 사용됩니다. 일회용 토큰에 사용하면 유용합니다.
     * <p>
     * JSON 웹 토큰은 어떻게 작동합니까?
     * <p>
     * 사용자가 보호된 경로 또는 리소스에 액세스하려고 할 때마다 사용자 에이전트는 일반적으로 Bearer 스키마 를 사용하여 Authorization 헤더 에서 JWT를 보내야 합니다. 헤더의 내용은 다음과 같아야 합니다.
     * <p>
     * Authorization: Bearer [token]
     * <p>
     * <p>
     * 출처 : jwt.io
     */

    // 자잘한 토큰 발급용 절대값 설정
    private static final String AUTHORITIES_KEY = "auth";
    private static final String BEARER_TYPE = "bearer";
    private static final long ACCESS_TOKEN_EXPIRE_TIME = 1000*60*30; // 밀리세컨드 30분
    private static final long REFRESH_TOKEN_EXPIRE_TIME = 1000*60*60*24*7; // 7일

    // DB에서 토큰 사용자를 확인할 예정이므로 멤버 리포지토리 인젝션함
    private final MemberRepository memberRepository;

    // 암호화된 키를 담을 객체 선언
    private final Key key;

    @Autowired
    public TokenProvider(@Value("#{environment['secret.key']}") String secretKey, MemberRepository memberRepository) {
        this.memberRepository = memberRepository;
        // 현재 정한 sercret_key는 BASE64 encoding으로 암호화 시켜놓은 상태이므로 풀어야함
        // Keys 메소드를 사용해 안전하게 암호화를 할 것이므로 byte[] 로 되어 있어야 함
        // io.jsonWebtoken.io.Decoders
        byte[] keyBytes = Decoders.BASE64.decode(secretKey);
        // Keys : SecretKeys 및 KeyPairs를 안전하게 생성하기 위한 유틸리티 클래스
        // io.jsonwebtoken.security.Keys; 키 바이트 배열 길이가 256비트(32바이트) 미만인 경우 @throws WeakKeyException;
        this.key = Keys.hmacShaKeyFor(keyBytes);
    }

    // 권한을 실어나를 토큰 Dto
    public TokenDto generateTokenDto(Authentication authentication) {
        String authorities = authentication
                .getAuthorities()
                        .stream()
                .map(GrantedAuthority::getAuthority)
                .collect(Collectors.joining(","));
        // 인증 정보에서 권한들 꺼내와서 스트림으로 만든담에 (GrantedAuthority.s)-> s.getAuthority() 로 변형
        // authorities 문자열에 , 로 나눠담음

        long nowTime = new Date().getTime();

        // 토큰 구성 { Header.Payload.signature_sign }
        // 여기서 payload 부분 세팅 후 암호화

        // access token 설정
        Date accessTokenExpires = new Date(nowTime + ACCESS_TOKEN_EXPIRE_TIME);
        String acessToken = Jwts.builder()
                // paload 부분에 필드, 값 넣기
                .setSubject(authentication.getName())
                .claim(AUTHORITIES_KEY, authorities)
                .setExpiration(accessTokenExpires)
                .signWith(key, SignatureAlgorithm.HS256)
                .compact();

        // refresh token 설정
        Date refreshTokenExpires = new Date(nowTime + REFRESH_TOKEN_EXPIRE_TIME);
        String refreshToken = Jwts.builder()
                .setSubject(authentication.getName())
                .setExpiration(refreshTokenExpires)
                .signWith(key, SignatureAlgorithm.HS256)
                .compact();

        return TokenDto.builder()
                .grantType(BEARER_TYPE)
                .accessToken(acessToken)
                .accessTokenExpiresIn(accessTokenExpires.getTime())
                .refreshToken(refreshToken)
                .build();

    }
    /**
     * AuthenticationManager.authenticate(Authentication) 메서드에서 요청을 처리한 후 인증 요청 또는 인증된 주체에 대한 토큰을 나타냅니다.
     * <p>
     * 요청이 인증되면 인증은 일반적으로 사용 중인 인증 메커니즘에 의해 SecurityContextHolder가 관리하는 스레드 로컬 SecurityContext에 저장됩니다
     * .
     * Spring Security의 인증 메커니즘 중 하나를 사용하지 않고 인증 인스턴스를 생성하고 코드를 사용하여 명시적 인증을 달성할 수 있습니다.
     * <p>
     * SecurityContext 컨텍스트 = SecurityContextHolder.createEmptyContext();
     * <p>
     * context.setAuthentication(인증);
     * <p>
     * SecurityContextHolder.setContext(컨텍스트);
     * <p>
     * 인증에 인증된 속성이 true로 설정되어 있지 않으면 이를 만나는 보안 인터셉터(메소드 또는 웹 호출용)에 의해 계속 인증됩니다.
     * <p>
     * 대부분의 경우 프레임워크는 보안 컨텍스트 및 인증 개체 관리를 투명하게 처리합니다.
     */

    // 받아온 토큰 PayLoad 복호화용 서비스 메소드
    private Claims parseClaims(String accessToken) {
        try {
            return Jwts.parserBuilder()
                    .setSigningKey(key)
                    // 복호화를 위한 키 입력
                    .build()
                    // 빌드
                    .parseClaimsJws(accessToken)
                    // payLoad 복호화 (xxxxx.yyyyy.zzzzz) -> (xxxxx.복호화.zzzzz)
                    // JWS (Json Web Signature) - 서버에서 인증을 근거로 인증정보를 서버의 private key로 서명 한것을 토큰화 한것
                    // 서버 시크릿 키 넣고 푸는 부분
                    .getBody();
                    // 풀었으면 가져옴
        } catch (ExpiredJwtException e) {
            return e.getClaims();
            // 토큰 기한이 만료되었을 경우
        }
    }

    // Jwt 필터를 통과했으면 Security라는 경비원이 지키고 있으므로 통과시켜줄 허가증을 발급받을 서비스 메소드가 핋요함
    // 복호화된 payLoad에서 유저 정보 획득 & DB랑 대조해서 맞으면 Security 인증용 토큰(허가증) 발급
    public Authentication getAuthentication(String accessToken) {
        Claims claims = parseClaims(accessToken);

        // PAYLOAD에 키값 중에 "auth"가 없으면
        if (claims.get(AUTHORITIES_KEY) == null) {
            throw new RuntimeException("권한 정보가 없는 토큰입니다");
        }

        // UserDetails 인터페이스의 구현명세를 친절하게 구현한 User를 안쓰고 굳이 Members를 사용하기로 했기 때문에 확실히 다 바꿔줘야함
        // (심지어 빌더 패턴으로 유저 계정 정지, 조회, 잠금, 비밀번호 암호화(PasswordEncoding)까지 전부 구현되어있음. 노션 자료의 의도를 알 수가 없다)
        String username = claims.getSubject();
        // Controller 보다 훨씬 앞선 필터 최전선에서 Transaction이 일어나고 있는데 이게 맞나 싶기도
        Member member = memberRepository.findByUsername(username)
                .orElseThrow(() -> new UsernameNotFoundException("Can't find " + username ));
        // AccessToken PAYLOAD에서 가져온 이름과 DB에서 가져온 멤버 이름이 일치하면
        MemberDetails memberDetails = new MemberDetails(member);
        // Security의 UsernamePasswordAuthenticationToken이 구현 명세로 인정하는(Object principal) UserDetails 인터페이스의 양식에 맞춰
        return new UsernamePasswordAuthenticationToken(memberDetails, null, memberDetails.getAuthorities());
        // MemberDetails 객체로 만들어서 Security 인증용 Authentication 리턴
    }


    // jwt 필터에서 사용할 유효성 검증에 필요한 서비스 메소드
    /**
     * Spring JWT 토큰 검증 예외 처리
     * <p>
     * ExpiredJwtException : JWT의 유효시간 초과했을 경우
     * <p>
     * UnsupportedJwtException : JWT의 형식이 일치 하지 않을 경우
     * <p>
     * MalformedJwtException : JWT가 올바르게 구성되지 않을 경우
     * <p>
     * SignatureException : JWT의 기존 signature 검증이 실패 했을 경우      -Deprecated-
     * <p>
     * PrematureJwtException : nbf를 선언했을 경우 토큰 유효 시간전에 사용했을 경우
     * <p>
     * ClaimJwtException : JWT에서 권한 Claim 검사를 실패했을 경우
     */
    public boolean validateToken(String jwt) {
        try {
            Jwts.parserBuilder()
                    // jwts 빌더 분석
                    .setSigningKey(key)
                    // HMAC-SHA algorithm으로 암호화 시켰던 서버 시크릿 키 값 넣어주고
                    .build()
                    // 다시 필드 값 세팅
                    .parseClaimsJws(jwt);
                    // 세팅된 시크릿 키로 가장 겉껍데기 복호화
                    return true;
                    // 예외처리 안뜨고 복호화가 된거면 일단 뭐가 들어있긴 한것이므로 true 리턴

            // 예외처리 뜨면 Jwts에서 잦됬음을 감지해서 알려준 것이므로 에러 문구를 커스텀한다
        } catch (io.jsonwebtoken.security.SecurityException | MalformedJwtException e) {
            log.info("JWT가 올바르게 구성되지 않았습니다");
        } catch (ExpiredJwtException e) {
            log.info("JWT의 유효시간이 초과되었습니다");
        } catch (UnsupportedJwtException e) {
            log.info("JWT의 형식이 일치 하지 않습니다");
        } catch (PrematureJwtException e) {
            log.info("이 토큰은 아직 유효한 토큰이 아닙니다. 활성화 시기를 확인해 주십시오");
        } catch (ClaimJwtException e) {
            log.info("Jwts의 PAYLOAD 분석에 실패했습니다");
        }
        return false;
    }
}
