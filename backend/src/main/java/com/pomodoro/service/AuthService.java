package com.pomodoro.service;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.pomodoro.common.Result;
import com.pomodoro.dto.AuthResponse;
import com.pomodoro.dto.LoginRequest;
import com.pomodoro.dto.RefreshRequest;
import com.pomodoro.dto.RegisterRequest;
import com.pomodoro.entity.User;
import com.pomodoro.entity.UserSettings;
import com.pomodoro.mapper.UserMapper;
import com.pomodoro.mapper.UserSettingsMapper;
import com.pomodoro.security.JwtUtil;
import lombok.RequiredArgsConstructor;
import org.springframework.dao.DuplicateKeyException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class AuthService {

    private final UserMapper userMapper;
    private final UserSettingsMapper userSettingsMapper;
    private final JwtUtil jwtUtil;
    private final PasswordEncoder passwordEncoder;

    @Transactional
    public Result<AuthResponse> register(RegisterRequest request) {
        if (userMapper.selectCount(new LambdaQueryWrapper<User>()
                .eq(User::getUsername, request.getUsername())) > 0) {
            return Result.error("Username already exists");
        }

        if (userMapper.selectCount(new LambdaQueryWrapper<User>()
                .eq(User::getEmail, request.getEmail())) > 0) {
            return Result.error("Email already exists");
        }

        User user = new User();
        user.setUsername(request.getUsername());
        user.setEmail(request.getEmail());
        user.setPasswordHash(passwordEncoder.encode(request.getPassword()));

        try {
            userMapper.insert(user);
        } catch (DuplicateKeyException e) {
            return Result.error("Username or email already exists");
        }

        UserSettings settings = new UserSettings();
        settings.setUserId(user.getId());
        userSettingsMapper.insert(settings);

        return Result.ok(buildAuthResponse(user));
    }

    public Result<AuthResponse> login(LoginRequest request) {
        User user = userMapper.selectOne(new LambdaQueryWrapper<User>()
                .eq(User::getUsername, request.getUsername()));

        if (user == null || !passwordEncoder.matches(request.getPassword(), user.getPasswordHash())) {
            return Result.error("Invalid username or password");
        }

        return Result.ok(buildAuthResponse(user));
    }

    public Result<AuthResponse> refresh(RefreshRequest request) {
        if (!jwtUtil.validateToken(request.getRefreshToken())) {
            return Result.error("Invalid or expired refresh token");
        }

        if (!jwtUtil.isRefreshToken(request.getRefreshToken())) {
            return Result.error("Invalid token type: expected refresh token");
        }

        Long userId = jwtUtil.getUserIdFromToken(request.getRefreshToken());

        User user = userMapper.selectById(userId);
        if (user == null) {
            return Result.error("User not found");
        }

        return Result.ok(buildAuthResponse(user));
    }

    public Result<User> getCurrentUser(Long userId) {
        User user = userMapper.selectById(userId);
        if (user == null) {
            return Result.error("User not found");
        }
        return Result.ok(user);
    }

    private AuthResponse buildAuthResponse(User user) {
        AuthResponse response = new AuthResponse();
        response.setAccessToken(jwtUtil.generateAccessToken(user.getId(), user.getUsername()));
        response.setRefreshToken(jwtUtil.generateRefreshToken(user.getId(), user.getUsername()));

        User safeUser = new User();
        safeUser.setId(user.getId());
        safeUser.setUsername(user.getUsername());
        safeUser.setEmail(user.getEmail());
        safeUser.setAvatarUrl(user.getAvatarUrl());
        safeUser.setDailyGoal(user.getDailyGoal());
        safeUser.setCreatedAt(user.getCreatedAt());
        safeUser.setUpdatedAt(user.getUpdatedAt());
        response.setUser(safeUser);

        return response;
    }
}
