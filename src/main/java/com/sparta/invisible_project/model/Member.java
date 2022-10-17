package com.sparta.invisible_project.model;

import com.fasterxml.jackson.annotation.JsonIgnore;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import javax.persistence.*;
import javax.validation.constraints.NotBlank;

@Setter
@Getter
@NoArgsConstructor
@Entity
public class Member extends Timestamped {

    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Id
    private Long member_id;

    @NotBlank(message = "아이디를 입력해주세요")
    @Column(nullable = false, unique = true)
    private String username;

    @NotBlank(message = "비밀번호를 입력해주세요")
    @Column(nullable = false)
    private String password;

    @JsonIgnore
    @Enumerated(EnumType.STRING)
    private Authority authority;

    public Member(String username, String password, Authority authority) {
        this.username = username;
        this.password = password;
        this.authority = authority;
    }
}
