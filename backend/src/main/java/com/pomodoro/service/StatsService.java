package com.pomodoro.service;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.pomodoro.common.Result;
import com.pomodoro.entity.PomodoroRecord;
import com.pomodoro.entity.PomodoroStatus;
import com.pomodoro.entity.Tag;
import com.pomodoro.mapper.PomodoroRecordMapper;
import com.pomodoro.mapper.TagMapper;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.time.DayOfWeek;
import java.time.LocalDate;
import java.time.LocalTime;
import java.time.YearMonth;
import java.time.temporal.TemporalAdjusters;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class StatsService {

    private final PomodoroRecordMapper pomodoroRecordMapper;
    private final TagMapper tagMapper;

    public Result<Map<String, Object>> daily(Long userId, LocalDate date) {
        List<PomodoroRecord> records = getCompletedRecords(userId, date, date);

        int completedCount = records.size();
        int totalMinutes = records.stream().mapToInt(PomodoroRecord::getDuration).sum() / 60;

        Map<Long, List<PomodoroRecord>> byTag = records.stream()
                .filter(r -> r.getTagId() != null)
                .collect(Collectors.groupingBy(PomodoroRecord::getTagId));

        List<Map<String, Object>> tagBreakdown = new ArrayList<>();
        for (Map.Entry<Long, List<PomodoroRecord>> entry : byTag.entrySet()) {
            Tag tag = tagMapper.selectById(entry.getKey());
            Map<String, Object> item = new HashMap<>();
            item.put("tagId", entry.getKey());
            item.put("tagName", tag != null ? tag.getName() : "Unknown");
            item.put("tagColor", tag != null ? tag.getColor() : "#999");
            item.put("count", entry.getValue().size());
            item.put("minutes", entry.getValue().stream().mapToInt(PomodoroRecord::getDuration).sum() / 60);
            tagBreakdown.add(item);
        }

        Map<String, Object> result = new HashMap<>();
        result.put("completedCount", completedCount);
        result.put("totalMinutes", totalMinutes);
        result.put("tagBreakdown", tagBreakdown);
        return Result.ok(result);
    }

    public Result<List<Map<String, Object>>> weekly(Long userId, LocalDate date) {
        LocalDate weekStart = date.with(TemporalAdjusters.previousOrSame(DayOfWeek.MONDAY));
        LocalDate weekEnd = weekStart.plusDays(6);

        List<Map<String, Object>> dailyBreakdown = new ArrayList<>();
        for (LocalDate d = weekStart; !d.isAfter(weekEnd); d = d.plusDays(1)) {
            List<PomodoroRecord> records = getCompletedRecords(userId, d, d);
            Map<String, Object> day = new HashMap<>();
            day.put("date", d.toString());
            day.put("completedCount", records.size());
            day.put("totalMinutes", records.stream().mapToInt(PomodoroRecord::getDuration).sum() / 60);
            dailyBreakdown.add(day);
        }

        return Result.ok(dailyBreakdown);
    }

    public Result<List<Map<String, Object>>> monthly(Long userId, YearMonth month) {
        LocalDate monthStart = month.atDay(1);
        LocalDate monthEnd = month.atEndOfMonth();

        List<Map<String, Object>> dailyBreakdown = new ArrayList<>();
        for (LocalDate d = monthStart; !d.isAfter(monthEnd); d = d.plusDays(1)) {
            List<PomodoroRecord> records = getCompletedRecords(userId, d, d);
            Map<String, Object> day = new HashMap<>();
            day.put("date", d.toString());
            day.put("completedCount", records.size());
            day.put("totalMinutes", records.stream().mapToInt(PomodoroRecord::getDuration).sum() / 60);
            dailyBreakdown.add(day);
        }

        return Result.ok(dailyBreakdown);
    }

    public Result<Map<String, Object>> overview(Long userId) {
        List<PomodoroRecord> allCompleted = pomodoroRecordMapper.selectList(
                new LambdaQueryWrapper<PomodoroRecord>()
                        .eq(PomodoroRecord::getUserId, userId)
                        .eq(PomodoroRecord::getStatus, PomodoroStatus.completed)
                        .orderByAsc(PomodoroRecord::getStartedAt));

        int totalPomodoros = allCompleted.size();
        int totalHours = allCompleted.stream().mapToInt(PomodoroRecord::getDuration).sum() / 3600;

        // Calculate streaks
        Map<LocalDate, Boolean> dayMap = allCompleted.stream()
                .collect(Collectors.toMap(
                        r -> r.getStartedAt().toLocalDate(),
                        r -> true,
                        (a, b) -> true));

        int currentStreak = 0;
        int bestStreak = 0;
        int tempStreak = 0;

        // Calculate current streak (working backwards from today)
        LocalDate today = LocalDate.now();
        LocalDate checkDate = today;
        while (dayMap.containsKey(checkDate)) {
            currentStreak++;
            checkDate = checkDate.minusDays(1);
        }

        // Calculate best streak
        if (!allCompleted.isEmpty()) {
            LocalDate firstDay = allCompleted.get(0).getStartedAt().toLocalDate();
            LocalDate lastDay = allCompleted.get(allCompleted.size() - 1).getStartedAt().toLocalDate();
            for (LocalDate d = firstDay; !d.isAfter(lastDay); d = d.plusDays(1)) {
                if (dayMap.containsKey(d)) {
                    tempStreak++;
                    bestStreak = Math.max(bestStreak, tempStreak);
                } else {
                    tempStreak = 0;
                }
            }
        }

        Map<String, Object> result = new HashMap<>();
        result.put("totalPomodoros", totalPomodoros);
        result.put("totalHours", totalHours);
        result.put("currentStreak", currentStreak);
        result.put("bestStreak", bestStreak);
        return Result.ok(result);
    }

    public List<PomodoroRecord> exportRecords(Long userId) {
        return pomodoroRecordMapper.selectList(
                new LambdaQueryWrapper<PomodoroRecord>()
                        .eq(PomodoroRecord::getUserId, userId)
                        .orderByDesc(PomodoroRecord::getStartedAt));
    }

    private List<PomodoroRecord> getCompletedRecords(Long userId, LocalDate startDate, LocalDate endDate) {
        return pomodoroRecordMapper.selectList(
                new LambdaQueryWrapper<PomodoroRecord>()
                        .eq(PomodoroRecord::getUserId, userId)
                        .eq(PomodoroRecord::getStatus, PomodoroStatus.completed)
                        .ge(PomodoroRecord::getStartedAt, startDate.atStartOfDay())
                        .le(PomodoroRecord::getStartedAt, endDate.atTime(LocalTime.MAX)));
    }
}
