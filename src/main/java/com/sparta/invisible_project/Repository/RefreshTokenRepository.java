package com.sparta.invisible_project.Repository;

import com.sparta.invisible_project.Entity.RefreshToken;
import org.springframework.data.jpa.repository.JpaRepository;

import java.sql.Ref;
import java.util.Optional;

public interface RefreshTokenRepository extends JpaRepository<RefreshToken, Long> {
    Optional<RefreshToken> findByMemberUsername(String username);
}

