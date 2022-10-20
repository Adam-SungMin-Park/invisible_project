package com.sparta.invisible_project.Entity;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.sparta.invisible_project.Dto.CommentDto;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import org.hibernate.annotations.Cascade;
import org.hibernate.annotations.CascadeType;
import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.annotation.LastModifiedDate;

import javax.persistence.*;
import javax.validation.constraints.NotBlank;

@Entity
@Getter
@AllArgsConstructor
@Table(name = "comments")
@NoArgsConstructor
public class Comment extends Timestamp  {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column
    @NotBlank(message = "content can't be blank")
    private String content;

    @Column
    private Long boardId;

    @JsonIgnore
    @ManyToOne
    @JoinColumn(name="member_username")
    private Member member;

    public Comment(CommentDto dto, Board board){
        this.content = dto.getContent();
        this.boardId = board.getId();
    }

    public void updateComment(CommentDto dto){
        this.content = dto.getContent() == null ? this.content : dto.getContent();
    }

}
