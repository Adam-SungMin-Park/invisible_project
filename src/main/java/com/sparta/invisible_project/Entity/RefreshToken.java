package com.sparta.invisible_project.Entity;

import lombok.AllArgsConstructor;
import lombok.Getter;

import javax.persistence.*;

@Getter
@Entity
public class RefreshToken {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column
    private String memberUsername;

    @Column
    private String refreshToken;

    public RefreshToken(){}

    public RefreshToken(String token, String username){
        this.refreshToken = token;
        this.memberUsername = username;
    }
    public RefreshToken update(String token){
        this.refreshToken = token;
        return this;
    }

}
