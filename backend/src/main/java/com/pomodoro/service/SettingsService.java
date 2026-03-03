package com.pomodoro.service;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.pomodoro.common.Result;
import com.pomodoro.dto.UpdateSettingsRequest;
import com.pomodoro.entity.UserSettings;
import com.pomodoro.mapper.UserSettingsMapper;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class SettingsService {

    private final UserSettingsMapper userSettingsMapper;

    public Result<UserSettings> get(Long userId) {
        UserSettings settings = userSettingsMapper.selectOne(
                new LambdaQueryWrapper<UserSettings>()
                        .eq(UserSettings::getUserId, userId));

        if (settings == null) {
            settings = new UserSettings();
            settings.setUserId(userId);
            userSettingsMapper.insert(settings);
        }

        return Result.ok(settings);
    }

    public Result<UserSettings> update(Long userId, UpdateSettingsRequest request) {
        UserSettings settings = userSettingsMapper.selectOne(
                new LambdaQueryWrapper<UserSettings>()
                        .eq(UserSettings::getUserId, userId));

        if (settings == null) {
            settings = new UserSettings();
            settings.setUserId(userId);
            userSettingsMapper.insert(settings);
        }

        if (request.getWorkDuration() != null) settings.setWorkDuration(request.getWorkDuration());
        if (request.getShortBreak() != null) settings.setShortBreak(request.getShortBreak());
        if (request.getLongBreak() != null) settings.setLongBreak(request.getLongBreak());
        if (request.getLongBreakInterval() != null) settings.setLongBreakInterval(request.getLongBreakInterval());
        if (request.getAutoStartBreak() != null) settings.setAutoStartBreak(request.getAutoStartBreak());
        if (request.getAutoStartWork() != null) settings.setAutoStartWork(request.getAutoStartWork());
        if (request.getSoundEnabled() != null) settings.setSoundEnabled(request.getSoundEnabled());
        if (request.getNotificationEnabled() != null) settings.setNotificationEnabled(request.getNotificationEnabled());

        userSettingsMapper.updateById(settings);
        return Result.ok(settings);
    }
}
