package com.sparta.invisible_project.Service;

import com.sparta.invisible_project.Dto.HeartDto;
import com.sparta.invisible_project.Dto.ResponseDto;
import com.sparta.invisible_project.Entity.Heart;
import com.sparta.invisible_project.Repository.HeartRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;

@Service
@Slf4j
@RequiredArgsConstructor
public class HeartService {
    private final HeartRepository heartRepository;

    @Transactional
    public ResponseDto<?> heart(HeartDto dto,  Long boardId,UserDetailsImpl userDetails){
        long memberId = userDetails.getMember().getMember_id();
        Optional<Heart> heartCheck = heartRepository.findHeartByMemberId(memberId);

        if(heartCheck.isPresent()){
            heartRepository.deleteHeartByMemberId(memberId);
        }
        dto.setBoard_id(boardId);
        dto.setMember_id(memberId);
        Heart heart = new Heart(dto);
        heartRepository.save(heart);

        return ResponseDto.success(heartRepository.save(heart));
    }

}
