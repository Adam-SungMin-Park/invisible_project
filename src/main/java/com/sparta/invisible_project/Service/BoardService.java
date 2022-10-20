package com.sparta.invisible_project.Service;

import com.sparta.invisible_project.Dto.BoardCommentHeartDto;
import com.sparta.invisible_project.Dto.BoardDto;
import com.sparta.invisible_project.Dto.LoginResponseDto;
import com.sparta.invisible_project.Dto.ResponseDto;
import com.sparta.invisible_project.Entity.Board;
import com.sparta.invisible_project.Entity.Comment;
import com.sparta.invisible_project.Entity.Heart;
import com.sparta.invisible_project.Entity.Member;
import com.sparta.invisible_project.Repository.BoardRepository;
import com.sparta.invisible_project.Repository.CommentRepository;
import com.sparta.invisible_project.Repository.HeartRepository;
import com.sparta.invisible_project.Repository.MemberRepository;
import lombok.RequiredArgsConstructor;
import org.apache.catalina.User;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;

import javax.servlet.http.HttpServletResponse;
import javax.transaction.Transactional;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import static com.sparta.invisible_project.Dto.ResponseDto.success;

@Service
@RequiredArgsConstructor
public class BoardService {

    private final BoardRepository boardRepository;
    private final MemberRepository memberRepository;
    private final HeartRepository heartRepository;

    private final CommentRepository commentRepository;

    @Transactional
    public ResponseEntity<Board> createBoard(BoardDto boardDto, UserDetailsImpl userDetails, Member member) {
        Board _board = boardRepository.save(new Board(boardDto));
        _board.setMemberId(userDetails.getMember().getMember_id());
        return new ResponseEntity<>(boardRepository.save(_board),HttpStatus.OK);
    }

    public ResponseEntity<List<Board>> getBoard() {
//        List<Board> boards = new ArrayList<Board>();
//        boards.addAll(boardRepository.findAll());



        return new ResponseEntity<>(boardRepository.findAll(), HttpStatus.OK);
    }

    public ResponseEntity<BoardCommentHeartDto> getBoardById(Long id, BoardCommentHeartDto dto, UserDetailsImpl userDetails){



        Board board = boardRepository.findById(id).orElseThrow(()-> new IllegalArgumentException("Board does not exist"));

        dto.setComment(board.getComments());
        dto.setContent(board.getContent());
        dto.setTitle(board.getTitle());
        long memberId = userDetails.getMember().getMember_id();
        System.out.println("memberId = " + memberId);
        Heart heart = heartRepository.findHeartByMemberId(memberId).orElseThrow(()->new IllegalArgumentException("no heart available at that member id"));
        List<Heart> heartList = new ArrayList<>();
        heartList.add(heart);



        dto.setHeartCount((long)heartList.size());


        return new ResponseEntity<>(dto,HttpStatus.OK);
    }


    @Transactional
    public ResponseEntity<Board> deleteBoard(Long id) {
        System.out.println("BoardService.deleteBoard");
        Board board = boardRepository.findById(id).orElseThrow(() -> new IllegalArgumentException("id not present"));
        boardRepository.delete(board);
        heartRepository.deleteHeartByBoardId(id);
        return new ResponseEntity<>(board, HttpStatus.OK);
    }

    @Transactional
    public ResponseEntity<Board> updateBoard(BoardDto dto ,Long id){
        Board board = boardRepository.findByMemberId(id).orElseThrow(()-> new IllegalArgumentException("Id not exist"));
        board.boardUpdate(dto);
        return new ResponseEntity<>(board,HttpStatus.OK);
    }

    public ResponseEntity<List<Board>> getMyBoard(Long userId)  {
        Member member = memberRepository.findById(userId).orElseThrow();
        System.out.println("member.getUsername() = " + member.getUsername());
        System.out.println(boardRepository.findAllByMember(member));
        return new ResponseEntity<>(boardRepository.findAllByMember(member),HttpStatus.OK);
    }
}
