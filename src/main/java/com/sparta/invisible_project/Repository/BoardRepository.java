package com.sparta.invisible_project.Repository;

import com.sparta.invisible_project.Entity.Board;
import com.sparta.invisible_project.Entity.Member;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

public interface BoardRepository extends JpaRepository<Board,Long> {
    List<Board> findAllByMember(Member member);
    Optional<Board> findByMemberId(Long id);

}
