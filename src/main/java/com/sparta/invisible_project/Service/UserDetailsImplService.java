package com.sparta.invisible_project.Service;

import com.sparta.invisible_project.Entity.Member;
import com.sparta.invisible_project.Repository.MemberRepository;
import com.sparta.invisible_project.Repository.RefreshTokenRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.context.annotation.Bean;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;


@Service
@RequiredArgsConstructor
public class UserDetailsImplService implements UserDetailsService {

    private final MemberRepository memberRepository;


//
//    public UserDetailsImplService(MemberRepository memberRepository){
//        this.memberRepository = memberRepository;
//    }


    @Override
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
        Member member = memberRepository.findByUsername(username).orElseThrow(
                ()-> new RuntimeException("member not available"));
//        UserDetailsImpl userDetails = new UserDetailsImpl();
//        userDetails.setMember(member);
        return new UserDetailsImpl(member);
    }
}
