package com.sparta.invisible_project.Repository;

import com.sparta.invisible_project.Dto.ResponseDto;
import com.sparta.invisible_project.Entity.Heart;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface HeartRepository extends JpaRepository<Heart,Long > {
    Optional<Heart> findHeartByMemberId(Long member_id);
    void deleteHeartByMemberId(Long member_id);
    void deleteHeartByBoardId(Long id);
}
