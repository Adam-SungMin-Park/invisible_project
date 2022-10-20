package com.sparta.invisible_project.Repository;

import com.sparta.invisible_project.Entity.Member;
import com.sparta.invisible_project.Entity.RefreshToken;
import org.springframework.data.jpa.repository.JpaRepository;


import java.util.Optional;

public interface MemberRepository extends JpaRepository<Member,Long> {
    Optional<Member> findByUsername(String username);

}
