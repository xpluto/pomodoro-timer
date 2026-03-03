package com.pomodoro.controller;

import com.pomodoro.common.Result;
import com.pomodoro.dto.UpdateSettingsRequest;
import com.pomodoro.entity.UserSettings;
import com.pomodoro.service.SettingsService;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/settings")
@RequiredArgsConstructor
public class SettingsController {

    private final SettingsService settingsService;

    @GetMapping
    public ResponseEntity<Result<UserSettings>> get(HttpServletRequest request) {
        Long userId = (Long) request.getAttribute("userId");
        return ResponseEntity.ok(settingsService.get(userId));
    }

    @PutMapping
    public ResponseEntity<Result<UserSettings>> update(@Valid @RequestBody UpdateSettingsRequest settingsRequest,
                                                        HttpServletRequest request) {
        Long userId = (Long) request.getAttribute("userId");
        return ResponseEntity.ok(settingsService.update(userId, settingsRequest));
    }
}
