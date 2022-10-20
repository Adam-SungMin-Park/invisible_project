package com.sparta.invisible_project.Repository;

import com.sparta.invisible_project.Entity.Comment;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface CommentRepository extends JpaRepository<Comment, Long> {

}
