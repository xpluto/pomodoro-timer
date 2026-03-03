package com.pomodoro.dto;

import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;
import lombok.Data;

@Data
public class UpdateSettingsRequest {

    @Min(60) @Max(7200)
    private Integer workDuration;

    @Min(60) @Max(3600)
    private Integer shortBreak;

    @Min(60) @Max(3600)
    private Integer longBreak;

    @Min(1) @Max(10)
    private Integer longBreakInterval;

    private Boolean autoStartBreak;

    private Boolean autoStartWork;

    private Boolean soundEnabled;

    private Boolean notificationEnabled;
}
