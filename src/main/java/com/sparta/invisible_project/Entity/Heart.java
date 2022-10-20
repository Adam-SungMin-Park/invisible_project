package com.sparta.invisible_project.Entity;

import com.sparta.invisible_project.Dto.HeartDto;
import lombok.Getter;
import lombok.NoArgsConstructor;

import javax.persistence.*;

@Entity
@Getter
@NoArgsConstructor
public class Heart {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private long id;

    @Column
    private long boardId;

    @Column
    private long memberId;

    public Heart(HeartDto dto) {
        this.boardId = dto.getBoard_id();
        this.memberId = dto.getMember_id();
    }
}
