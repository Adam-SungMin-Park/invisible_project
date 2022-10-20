package com.sparta.invisible_project.Controller;

import com.sparta.invisible_project.Dto.BoardCommentHeartDto;
import com.sparta.invisible_project.Dto.BoardDto;
import com.sparta.invisible_project.Dto.LoginResponseDto;
import com.sparta.invisible_project.Dto.ResponseDto;
import com.sparta.invisible_project.Entity.Board;
import com.sparta.invisible_project.Entity.Member;
import com.sparta.invisible_project.Jwt.JwtUtil;
import com.sparta.invisible_project.Service.BoardService;
import com.sparta.invisible_project.Service.UserDetailsImpl;
import lombok.RequiredArgsConstructor;
import org.apache.coyote.Response;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpServletResponse;
import java.util.ArrayList;
import java.util.List;

@RequestMapping("/api/auth")
@RestController
@RequiredArgsConstructor
public class BoardController {

    private final BoardService boardService;

    @GetMapping("/boards")
    public ResponseEntity<List<Board>> getBoard(){
        List<Board> boards = new ArrayList<Board>();
        return boardService.getBoard();
    }
    @GetMapping("/boards/{id}")
    public ResponseEntity<BoardCommentHeartDto> getBoardById(@PathVariable Long id, BoardCommentHeartDto dto, @AuthenticationPrincipal UserDetailsImpl userDetails){
        return boardService.getBoardById(id,dto,userDetails);
    }

    @PostMapping("/boards/post")
    public ResponseEntity<Board> createBoard(@RequestBody BoardDto boardDto,@AuthenticationPrincipal UserDetailsImpl userDetails, Member member){
        return boardService.createBoard(boardDto,userDetails, member);
    }

    @DeleteMapping("/boards/delete/{id}")
    public ResponseEntity<Board> deleteBoard(@PathVariable Long id){
        return boardService.deleteBoard(id);
    }
    @PutMapping("/boards/update/{id}")
    public ResponseEntity<Board> updateBoard(@PathVariable Long id,@RequestBody BoardDto dto){
        return boardService.updateBoard(dto, id);
    }

}
