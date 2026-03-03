package com.pomodoro.dto;

import lombok.Data;

@Data
public class TimerSyncMessage {

    private Long userId;
    private String status;
    private Integer remainingSeconds;
    private String taskName;
}
