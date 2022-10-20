package com.sparta.invisible_project.Entity;


import com.fasterxml.jackson.annotation.JsonIgnore;
import com.sparta.invisible_project.Dto.BoardDto;
import com.sparta.invisible_project.Dto.CommentDto;
import com.sparta.invisible_project.Service.UserDetailsImpl;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.annotation.LastModifiedDate;

import javax.persistence.*;
import javax.validation.constraints.NotBlank;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;

@Getter
@Entity
@Setter
@NoArgsConstructor
public class Board extends Timestamp {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column
    @NotBlank(message = "content can't be blank")
    private String content;

    @Column
    @NotBlank(message = "title can't be blank")
    private String title;

    @JsonIgnore
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn
    private Member member;

    @Column
    private long memberId;

    @OneToMany( cascade = CascadeType.ALL)
    @JoinColumn(name = "comment_id")
    private  List<Comment> comments;

    public Board (BoardDto dto){
        this.content = dto.getContent();
        this.title = dto.getTitle();
    }

    public void boardUpdate(BoardDto dto){
        this.content = dto.getContent() == null ? this.content : dto.getContent();
        this.title = dto.getTitle() == null ? this.title : dto.getTitle();
    }



}
