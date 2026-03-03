package com.pomodoro.service;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.pomodoro.common.Result;
import com.pomodoro.dto.StartPomodoroRequest;
import com.pomodoro.entity.PomodoroRecord;
import com.pomodoro.entity.PomodoroStatus;
import com.pomodoro.mapper.PomodoroRecordMapper;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.time.temporal.ChronoUnit;
import java.util.List;

@Service
@RequiredArgsConstructor
public class PomodoroService {

    private final PomodoroRecordMapper pomodoroRecordMapper;

    public Result<PomodoroRecord> start(Long userId, StartPomodoroRequest request) {
        PomodoroRecord record = new PomodoroRecord();
        record.setUserId(userId);
        record.setTaskName(request.getTaskName());
        record.setTagId(request.getTagId());
        record.setPlannedDuration(request.getPlannedDuration());
        record.setDuration(0);
        record.setStartedAt(LocalDateTime.now());
        pomodoroRecordMapper.insert(record);
        return Result.ok(record);
    }

    public Result<PomodoroRecord> complete(Long userId, Long id) {
        return finishRecord(userId, id, PomodoroStatus.completed);
    }

    public Result<PomodoroRecord> interrupt(Long userId, Long id) {
        return finishRecord(userId, id, PomodoroStatus.interrupted);
    }

    public Result<PomodoroRecord> abandon(Long userId, Long id) {
        PomodoroRecord record = pomodoroRecordMapper.selectById(id);
        if (record == null || !record.getUserId().equals(userId)) {
            return Result.error("Record not found");
        }
        record.setStatus(PomodoroStatus.abandoned);
        record.setFinishedAt(LocalDateTime.now());
        pomodoroRecordMapper.updateById(record);
        return Result.ok(record);
    }

    public Result<Page<PomodoroRecord>> list(Long userId, int page, int size,
                                              LocalDate startDate, LocalDate endDate, Long tagId) {
        Page<PomodoroRecord> pageParam = new Page<>(page, size);
        LambdaQueryWrapper<PomodoroRecord> wrapper = new LambdaQueryWrapper<PomodoroRecord>()
                .eq(PomodoroRecord::getUserId, userId)
                .ge(startDate != null, PomodoroRecord::getStartedAt, startDate != null ? startDate.atStartOfDay() : null)
                .le(endDate != null, PomodoroRecord::getStartedAt, endDate != null ? endDate.atTime(LocalTime.MAX) : null)
                .eq(tagId != null, PomodoroRecord::getTagId, tagId)
                .orderByDesc(PomodoroRecord::getStartedAt);
        return Result.ok(pomodoroRecordMapper.selectPage(pageParam, wrapper));
    }

    public Result<List<PomodoroRecord>> todayRecords(Long userId) {
        LocalDateTime startOfDay = LocalDate.now().atStartOfDay();
        LocalDateTime endOfDay = LocalDate.now().atTime(LocalTime.MAX);

        List<PomodoroRecord> records = pomodoroRecordMapper.selectList(
                new LambdaQueryWrapper<PomodoroRecord>()
                        .eq(PomodoroRecord::getUserId, userId)
                        .ge(PomodoroRecord::getStartedAt, startOfDay)
                        .le(PomodoroRecord::getStartedAt, endOfDay)
                        .orderByDesc(PomodoroRecord::getStartedAt));

        return Result.ok(records);
    }

    private Result<PomodoroRecord> finishRecord(Long userId, Long id, PomodoroStatus status) {
        PomodoroRecord record = pomodoroRecordMapper.selectById(id);
        if (record == null || !record.getUserId().equals(userId)) {
            return Result.error("Record not found");
        }
        LocalDateTime now = LocalDateTime.now();
        record.setStatus(status);
        record.setFinishedAt(now);
        record.setDuration((int) ChronoUnit.SECONDS.between(record.getStartedAt(), now));
        pomodoroRecordMapper.updateById(record);
        return Result.ok(record);
    }
}
