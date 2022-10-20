package com.sparta.invisible_project.Dto;

import com.sparta.invisible_project.Entity.Comment;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;

import java.util.List;

@AllArgsConstructor
@Getter
@Setter
public class BoardCommentHeartDto {
    private String content;
    private String title;
    private List<Comment> comment;
    private Long heartCount;

}
