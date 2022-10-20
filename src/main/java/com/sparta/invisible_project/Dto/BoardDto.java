package com.sparta.invisible_project.Dto;


import com.sparta.invisible_project.Entity.Board;
import com.sparta.invisible_project.Entity.Member;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.RequiredArgsConstructor;

import javax.validation.constraints.NotBlank;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

@Getter
@NoArgsConstructor
public class BoardDto {
    @NotBlank(message = "title can't be blank")
    private String title;
    @NotBlank(message = "content can't be blank")
    private String content;

    private Member member;
    //private String author



//
//    public BoardDto(Board board) {
//        this.title = board.getTitle();
//        this.content = board.getContent();
//        this.author = board.getMember().getUsername();
//    }


//    public BoardDto(String title, String content, Member member, int heartCount) {
//        this.title = title;
//        this.content = content;
//        this.heartCount = heartCount;
//    }

    public BoardDto(String title, String content, Member member) {
        this.title = title;
        this.content = content;
        this.member = member;
    }

}
