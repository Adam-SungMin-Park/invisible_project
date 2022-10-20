package com.sparta.invisible_project.Controller;

import com.sparta.invisible_project.Dto.HeartDto;
import com.sparta.invisible_project.Dto.ResponseDto;
import com.sparta.invisible_project.Service.HeartService;
import com.sparta.invisible_project.Service.UserDetailsImpl;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/auth/heart")
public class HeartController {
    private final HeartService heartService;
    @PostMapping("/like/{boardId}")
    public ResponseDto<HeartDto> heart(HeartDto heartDto, @PathVariable Long boardId,@AuthenticationPrincipal UserDetailsImpl userDetails){
        heartService.heart(heartDto,boardId,userDetails);
        return  ResponseDto.success(heartDto);
    }
}
