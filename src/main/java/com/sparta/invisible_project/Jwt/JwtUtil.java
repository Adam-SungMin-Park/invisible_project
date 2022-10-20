package com.sparta.invisible_project.Jwt;


import com.sparta.invisible_project.Entity.RefreshToken;
import com.sparta.invisible_project.Dto.TokenDto;
import com.sparta.invisible_project.Repository.RefreshTokenRepository;
import io.jsonwebtoken.*;
import io.jsonwebtoken.security.Keys;
import io.jsonwebtoken.security.SignatureException;
import lombok.RequiredArgsConstructor;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.stereotype.Component;

import javax.annotation.PostConstruct;
import javax.servlet.http.HttpServletRequest;
import java.security.Key;
import java.time.Instant;
import java.util.*;

@Slf4j
@Component
@RequiredArgsConstructor
public class JwtUtil {
    private final RefreshTokenRepository refreshTokenRepository;
    private final UserDetailsService userDetailsService;
    public static final String ACCESS_TOKEN = "Access_Token";
    public static final String REFRESH_TOKEN= "Refresh_Token";
    private static final long ACCESS_TIME = 1000L * 60 * 60 * 24;
    private static final long REFRESH_TIME = 1000L * 60 * 60 * 365;



    @Value("${jwt.secret.key}")
    private String secretKey;
    private Key key;
    private final SignatureAlgorithm signatureAlgorithm = SignatureAlgorithm.HS256;
    @PostConstruct
    public void init(){
        byte[] bytes = Base64.getDecoder().decode(secretKey);
        key = Keys.hmacShaKeyFor(bytes);
    }


    // header token get method
    public String getHeaderToken(HttpServletRequest request, String type){

        return type.equals("Access") ? request.getHeader(ACCESS_TOKEN) : request.getHeader(REFRESH_TOKEN);
    }


    //token creation

    public TokenDto createAllToken(String username){
        return new TokenDto(createToken(username, "Access"),createToken(username, "Refresh"));
    }

    public String createToken(String username, String type){
        Date date = new Date();
        Calendar calendar = Calendar.getInstance(TimeZone.getTimeZone("Asia/Seoul"));

        long time = type.equals("Access") ? ACCESS_TIME : REFRESH_TIME;


       return Jwts.builder()
                .setSubject(username)
                .setExpiration(new Date(date.getTime()+time))
                .setIssuedAt(date)
                .signWith(key, signatureAlgorithm)
                .compact();
    }

    // token verification
    public Boolean tokenValidation(String token){
        try{
            Jwts.parserBuilder().setSigningKey(key).build().parseClaimsJws(token);
            return true;
        }catch(ExpiredJwtException | UnsupportedJwtException | MalformedJwtException | SignatureException | IllegalArgumentException ex){
            log.error(ex.getMessage());
            return false;
        }
    }


    // refresher token verification
    public Boolean refreshTokenValidation(String token){
       //1st validation
        if(!tokenValidation(token)){
           return false;
       }
        //DB token comparison
        Optional<RefreshToken> refreshToken = refreshTokenRepository.findByMemberUsername(getUsernameFromToken(token));
        return refreshToken.isPresent() && token.equals(refreshToken.get().getRefreshToken());
    }

    //make authentication object
    public Authentication createAuthentication(String username){
        UserDetails userDetails = userDetailsService.loadUserByUsername(username);
        return new UsernamePasswordAuthenticationToken(userDetails,"",userDetails.getAuthorities());
    }



    // get email from the token
    public String getUsernameFromToken(String token){
        return Jwts.parserBuilder().setSigningKey(key).build().parseClaimsJws(token).getBody().getSubject();
    }





}
