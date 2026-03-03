package com.pomodoro.service;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.pomodoro.common.Result;
import com.pomodoro.entity.PomodoroRecord;
import com.pomodoro.entity.PomodoroStatus;
import com.pomodoro.entity.User;
import com.pomodoro.mapper.PomodoroRecordMapper;
import com.pomodoro.mapper.UserMapper;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.time.DayOfWeek;
import java.time.LocalDate;
import java.time.LocalTime;
import java.time.temporal.TemporalAdjusters;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class LeaderboardService {

    private final PomodoroRecordMapper pomodoroRecordMapper;
    private final UserMapper userMapper;

    public Result<List<Map<String, Object>>> daily() {
        LocalDate today = LocalDate.now();
        return buildLeaderboard(today, today);
    }

    public Result<List<Map<String, Object>>> weekly() {
        LocalDate today = LocalDate.now();
        LocalDate weekStart = today.with(TemporalAdjusters.previousOrSame(DayOfWeek.MONDAY));
        return buildLeaderboard(weekStart, today);
    }

    private Result<List<Map<String, Object>>> buildLeaderboard(LocalDate startDate, LocalDate endDate) {
        List<PomodoroRecord> records = pomodoroRecordMapper.selectList(
                new LambdaQueryWrapper<PomodoroRecord>()
                        .eq(PomodoroRecord::getStatus, PomodoroStatus.completed)
                        .ge(PomodoroRecord::getStartedAt, startDate.atStartOfDay())
                        .le(PomodoroRecord::getStartedAt, endDate.atTime(LocalTime.MAX)));

        Map<Long, List<PomodoroRecord>> byUser = records.stream()
                .collect(Collectors.groupingBy(PomodoroRecord::getUserId));

        List<Map.Entry<Long, List<PomodoroRecord>>> sorted = byUser.entrySet().stream()
                .sorted((a, b) -> Integer.compare(b.getValue().size(), a.getValue().size()))
                .limit(20)
                .toList();

        List<Map<String, Object>> leaderboard = new ArrayList<>();
        int rank = 1;
        for (Map.Entry<Long, List<PomodoroRecord>> entry : sorted) {
            User user = userMapper.selectById(entry.getKey());
            int totalMinutes = entry.getValue().stream()
                    .mapToInt(PomodoroRecord::getDuration).sum() / 60;
            Map<String, Object> item = new HashMap<>();
            item.put("rank", rank++);
            item.put("username", user != null ? user.getUsername() : "Unknown");
            item.put("avatarUrl", user != null ? user.getAvatarUrl() : null);
            item.put("pomodoroCount", entry.getValue().size());
            item.put("totalMinutes", totalMinutes);
            leaderboard.add(item);
        }

        return Result.ok(leaderboard);
    }
}
