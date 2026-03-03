package com.pomodoro.dto;

import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.Data;

@Data
public class StartPomodoroRequest {

    @NotNull
    @Min(60)
    @Max(7200)
    private Integer plannedDuration;

    @Size(max = 200)
    private String taskName;

    private Long tagId;
}
