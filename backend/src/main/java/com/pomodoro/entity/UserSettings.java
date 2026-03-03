package com.pomodoro.entity;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;

@Data
@TableName("user_settings")
public class UserSettings {

    @TableId(type = IdType.AUTO)
    private Long id;

    private Long userId;

    private Integer workDuration;

    private Integer shortBreak;

    private Integer longBreak;

    private Integer longBreakInterval;

    private Boolean autoStartBreak;

    private Boolean autoStartWork;

    private Boolean soundEnabled;

    private Boolean notificationEnabled;
}
