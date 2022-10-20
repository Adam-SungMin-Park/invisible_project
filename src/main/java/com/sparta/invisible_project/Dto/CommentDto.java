package com.sparta.invisible_project.Dto;

import com.sparta.invisible_project.Entity.Board;
import com.sparta.invisible_project.Entity.Comment;
import com.sparta.invisible_project.Entity.Member;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.RequiredArgsConstructor;

import javax.validation.constraints.NotBlank;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

@Getter
@NoArgsConstructor
public class CommentDto {
    private Long id;
    @NotBlank(message = "content can't be blank")
    private String content;
    private Member member;
    private Board board;
}


