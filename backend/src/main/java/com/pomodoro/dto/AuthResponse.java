package com.pomodoro.dto;

import com.pomodoro.entity.User;
import lombok.Data;

@Data
public class AuthResponse {

    private String accessToken;
    private String refreshToken;
    private User user;
}
