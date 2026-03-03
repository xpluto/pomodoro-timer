package com.pomodoro.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;
import lombok.Data;

@Data
public class UpdateTagRequest {

    @NotBlank
    @Size(max = 50)
    private String name;

    @NotBlank
    @Pattern(regexp = "^#[0-9A-Fa-f]{6}$", message = "Color must be a valid hex color (e.g. #FF6B6B)")
    private String color;
}
