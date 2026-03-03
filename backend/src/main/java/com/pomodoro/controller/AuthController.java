package com.pomodoro.controller;

import com.pomodoro.common.Result;
import com.pomodoro.dto.AuthResponse;
import com.pomodoro.dto.LoginRequest;
import com.pomodoro.dto.RefreshRequest;
import com.pomodoro.dto.RegisterRequest;
import com.pomodoro.entity.User;
import com.pomodoro.service.AuthService;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/auth")
@RequiredArgsConstructor
public class AuthController {

    private final AuthService authService;

    @PostMapping("/register")
    public ResponseEntity<Result<AuthResponse>> register(@Valid @RequestBody RegisterRequest request) {
        Result<AuthResponse> result = authService.register(request);
        if (!result.isSuccess()) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(result);
        }
        return ResponseEntity.ok(result);
    }

    @PostMapping("/login")
    public ResponseEntity<Result<AuthResponse>> login(@Valid @RequestBody LoginRequest request) {
        Result<AuthResponse> result = authService.login(request);
        if (!result.isSuccess()) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(result);
        }
        return ResponseEntity.ok(result);
    }

    @PostMapping("/refresh")
    public ResponseEntity<Result<AuthResponse>> refresh(@Valid @RequestBody RefreshRequest request) {
        Result<AuthResponse> result = authService.refresh(request);
        if (!result.isSuccess()) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(result);
        }
        return ResponseEntity.ok(result);
    }

    @GetMapping("/me")
    public ResponseEntity<Result<User>> me(HttpServletRequest request) {
        Long userId = (Long) request.getAttribute("userId");
        if (userId == null) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(Result.error("Authentication required"));
        }
        return ResponseEntity.ok(authService.getCurrentUser(userId));
    }
}
