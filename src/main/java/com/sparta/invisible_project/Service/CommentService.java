package com.sparta.invisible_project.Service;

import com.sparta.invisible_project.Dto.BoardDto;
import com.sparta.invisible_project.Dto.CommentDto;
import com.sparta.invisible_project.Dto.ResponseDto;
import com.sparta.invisible_project.Entity.Board;
import com.sparta.invisible_project.Entity.Comment;
import com.sparta.invisible_project.Repository.BoardRepository;
import com.sparta.invisible_project.Repository.CommentRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;

import javax.servlet.http.HttpServletResponse;
import javax.transaction.Transactional;
import java.util.List;

@Service
@RequiredArgsConstructor
public class CommentService {
    private final CommentRepository commentRepository;
    private final BoardRepository boardRepository;

    public ResponseEntity<List<Comment>> getComment() {
        return new ResponseEntity<>(commentRepository.findAll(),HttpStatus.OK);
    }

    @Transactional
    public ResponseEntity<Comment> createComment(Long id, CommentDto dto, UserDetailsImpl userDetails) {
        Board board = boardRepository.findById(id).orElseThrow(()->new IllegalArgumentException("board with such Id does not exist"));

        if(board.getMemberId()!=(userDetails.getMember().getMember_id())){
            throw new IllegalArgumentException("you arent authorize to leave a comment");
        }

        Comment comment = new Comment(dto,board);
        board.getComments().add(comment);

        return new ResponseEntity<>(comment, HttpStatus.OK);
    }
    @Transactional
    public ResponseEntity<Comment> updateComment(Long id, CommentDto dto){
        Comment comment = commentRepository.findById(id).orElseThrow(()-> new IllegalArgumentException("no comment exist with following id "+ id));
        return new ResponseEntity<>(comment, HttpStatus.OK);
        
    }
}
