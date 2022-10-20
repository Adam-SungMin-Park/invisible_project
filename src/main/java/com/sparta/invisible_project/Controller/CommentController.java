package com.sparta.invisible_project.Controller;

import com.sparta.invisible_project.Dto.CommentDto;
import com.sparta.invisible_project.Dto.ResponseDto;
import com.sparta.invisible_project.Entity.Comment;
import com.sparta.invisible_project.Service.CommentService;
import com.sparta.invisible_project.Service.UserDetailsImpl;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Required;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpServletResponse;
import java.util.List;

@RestController
@RequestMapping("/api/auth/board")
@RequiredArgsConstructor
public class CommentController {
    private final CommentService commentService;

    @GetMapping("/comment")
    public ResponseEntity<List<Comment>> getComment(){
        return commentService.getComment();
    }

    @PostMapping("/comment/{id}")
    public ResponseEntity<Comment> createComment(@PathVariable Long id, @RequestBody CommentDto dto,@AuthenticationPrincipal UserDetailsImpl userDetails){
        return  commentService.createComment(id,dto,userDetails);
    }

    @PutMapping("/comment/update/{id}")
    public ResponseEntity<Comment> updateComment(@PathVariable Long id, @RequestBody CommentDto dto){
        return commentService.updateComment(id,dto);
    }
}
