package com.pomodoro.entity;

import com.baomidou.mybatisplus.annotation.FieldFill;
import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;

import java.time.LocalDateTime;

@Data
@TableName("pomodoro_records")
public class PomodoroRecord {

    @TableId(type = IdType.AUTO)
    private Long id;

    private Long userId;

    private Long tagId;

    private String taskName;

    private Integer duration;

    private Integer plannedDuration;

    private PomodoroStatus status;

    private LocalDateTime startedAt;

    private LocalDateTime finishedAt;

    @TableField(fill = FieldFill.INSERT)
    private LocalDateTime createdAt;
}
