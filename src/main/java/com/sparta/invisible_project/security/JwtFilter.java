package com.sparta.invisible_project.security;

import lombok.RequiredArgsConstructor;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.util.StringUtils;
import org.springframework.web.filter.OncePerRequestFilter;

import javax.servlet.FilterChain;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;


// 토큰 Provider를 만들어 security가 관리하는 근본인 ServletChainFilter 시스템에 합류시켜주려면 당연히 똑같은 모습으로 포장되어야함
// 여기서 filter로서 하는 역할을 서비스하게함
@RequiredArgsConstructor
public class JwtFilter extends OncePerRequestFilter {

    // 헤더에 넣을 필드 네임과 타입을 정해준다
    public static final String AUTHORIZATION_HEADER = "Authorization";
    public static final String BEARER_PREFIX = "Bearer ";

    private final TokenProvider tokenProvider;

    /**
     * 일반적으로 토큰은 요청 헤더의 Authorization 필드에 담아져 보내집니다.
     * <p>
     * Authorization: <type> <credentials>
     * <p>
     * 우리가 궁금해하던 bearer는 위 형식에서 type에 해당합니다. 토큰에는 많은 종류가 있고 서버는 다양한 종류의 토큰을 처리하기 위해 전송받은 type에 따라 토큰을 다르게 처리합니다.
     * <p>
     * 인증 타입
     * <p>
     * Basic -
     * 사용자 아이디와 암호를 Base64로 인코딩한 값을 토큰으로 사용한다. (RFC 7617)
     * <p>
     * Bearer -
     * JWT 혹은 OAuth에 대한 토큰을 사용한다. (RFC 6750)
     * <p>
     * Digest -
     * 서버에서 난수 데이터 문자열을 클라이언트에 보낸다. 클라이언트는 사용자 정보와 nonce를 포함하는 해시값을 사용하여 응답한다 (RFC 7616)
     * <p>
     * HOBA -
     * 전자 서명 기반 인증 (RFC 7486)
     * <p>
     * Mutual -
     * 암호를 이용한 클라이언트-서버 상호 인증 (draft-ietf-httpauth-mutual)
     * <p>
     * AWS4-HMAC-SHA256 -
     * AWS 전자 서명 기반 인증 (링크)
     * <p>
     * 위 인증 타입의 종류에서 나타난 것 처럼, bearer는 JWT와 OAuth를 타나내는 인증 타입입니다.
     */

    // 헤더의 Authorization 필드의 Bearer{whiteSpace} + 토큰값 에서 토큰값만 떼어오는 서비스 메소드
    private String resolveToken(HttpServletRequest req) {
        String bearerToken = req.getHeader(AUTHORIZATION_HEADER);
        if(StringUtils.hasText(bearerToken) && bearerToken.startsWith(BEARER_PREFIX)) {
            // 토큰이 "", Null이 아니고 String을 포함하고 있으면 true 반환, 그리고 bearer 로 시작하면 true 반환
            return bearerToken.substring(7);
            // 앞에 bearer에 빈공백 1개까지 무시하고 뒤에거 토큰 가져온다
        }
        return null;
    }

    /**
     * Authentication
     * AuthenticationManager.authenticate(Authentication) 메서드에서 요청을 처리한 후 인증 요청 또는 인증된 주체에 대한 토큰을 나타냅니다.
     * 요청이 인증되면 인증은 일반적으로 사용 중인 인증 메커니즘에 의해 SecurityContextHolder가 관리하는 스레드 로컬 SecurityContext에 저장됩니다.
     * Spring Security의 인증 메커니즘 중 하나를 사용하지 않고 인증 인스턴스를 생성하고 코드를 사용하여 명시적 인증을 달성할 수 있습니다.
     *   SecurityContext 컨텍스트 = SecurityContextHolder.createEmptyContext();
     *   context.setAuthentication(인증);
     *   SecurityContextHolder.setContext(컨텍스트);
     * <p>
     * 인증에 인증된 속성이 true로 설정되어 있지 않으면 이를 만나는 보안 인터셉터(메소드 또는 웹 호출용)에 의해 계속 인증됩니다.
     * 대부분의 경우 프레임워크는 보안 컨텍스트 및 인증 개체 관리를 투명하게 처리합니다.
     */

    // 실제 필터링 로직이 돌아가는 곳. 반드시 구현하도록 되어있다.
    // 인자로 받는 값들은 Servlet만을 사용한 웹개발을 할때와 완전히 동일하다 (왜냐하면 이 부분은 서블렛이니까)
    @Override
    protected void doFilterInternal(HttpServletRequest req, HttpServletResponse res, FilterChain filterChain)
            throws ServletException, IOException {

        // 토큰 꺼내기. request 헤더에 들어있을테니 꺼내오는 서비스 메소드를 위에 만든거임
        String jwt = resolveToken(req);

        // 토큰 유효성 검사
        // 검증용 서비스 메소드(validateToken)를 만들어야함(Provider에 있다) + authentication 가져오는 getter 만들어야함.
        if (StringUtils.hasText(jwt) && tokenProvider.validateToken(jwt)) {
            Authentication authentication = tokenProvider.getAuthentication(jwt);
            // authentication에는 UsernamePasswordAuthenticationToken(memberDetails, null, memberDetails.getAuthorities())이 담기게 됨
            // jwt가 빈 "" 값이나 null 값이 아닌지 str이 포함되어 있는지 삼중 확인하고 유효성 검사도 거쳤으니
            // 그걸 인증용 authentication에 담아주었다. 이제 이걸 SecurityContextHolder에 넣어준다.
            SecurityContextHolder.getContext().setAuthentication(authentication);
            // 여기서 JWT + Security 연동의 핵심 중 하나인 SecurityContext(인증된 사용자 목록)에 토큰 사용자를 넣어준다
            // authentication 정보를 세팅한 컨텍스트가 하나있고 그 컨텍스트 객체들이 홀더(지갑)에 차곡차곡 쌓여있다는걸 알 수 있듬
        }

        filterChain.doFilter(req,res);
        // 여기서 JWT와 Security의 연동은 마무리된다. doFilter를 통해 SecurityChainFilter에 큐로 연계된 다음 필터로 보내서
        // 자연스럽게 원래 있었던 필터인 마냥 물 흐르듯 진행되도록 한다
    }
}
