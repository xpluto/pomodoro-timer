package com.pomodoro.controller;

import com.pomodoro.common.Result;
import com.pomodoro.entity.PomodoroRecord;
import com.pomodoro.service.StatsService;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.io.IOException;
import java.io.PrintWriter;
import java.time.LocalDate;
import java.time.YearMonth;
import java.time.format.DateTimeFormatter;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/stats")
@RequiredArgsConstructor
public class StatsController {

    private final StatsService statsService;

    @GetMapping("/daily")
    public ResponseEntity<Result<Map<String, Object>>> daily(
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate date,
            HttpServletRequest request) {
        Long userId = (Long) request.getAttribute("userId");
        return ResponseEntity.ok(statsService.daily(userId, date));
    }

    @GetMapping("/weekly")
    public ResponseEntity<Result<List<Map<String, Object>>>> weekly(
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate date,
            HttpServletRequest request) {
        Long userId = (Long) request.getAttribute("userId");
        return ResponseEntity.ok(statsService.weekly(userId, date));
    }

    @GetMapping("/monthly")
    public ResponseEntity<Result<List<Map<String, Object>>>> monthly(
            @RequestParam String month,
            HttpServletRequest request) {
        Long userId = (Long) request.getAttribute("userId");
        YearMonth yearMonth = YearMonth.parse(month, DateTimeFormatter.ofPattern("yyyy-MM"));
        return ResponseEntity.ok(statsService.monthly(userId, yearMonth));
    }

    @GetMapping("/overview")
    public ResponseEntity<Result<Map<String, Object>>> overview(HttpServletRequest request) {
        Long userId = (Long) request.getAttribute("userId");
        return ResponseEntity.ok(statsService.overview(userId));
    }

    @GetMapping("/export")
    public void export(HttpServletRequest request,
                       HttpServletResponse response) throws IOException {
        Long userId = (Long) request.getAttribute("userId");
        List<PomodoroRecord> records = statsService.exportRecords(userId);

        response.setContentType("text/csv");
        response.setHeader("Content-Disposition", "attachment; filename=pomodoro_records.csv");

        PrintWriter writer = response.getWriter();
        writer.println("id,task_name,duration,planned_duration,status,started_at,finished_at");
        for (PomodoroRecord record : records) {
            writer.printf("%d,%s,%d,%d,%s,%s,%s%n",
                    record.getId(),
                    escapeCsv(record.getTaskName()),
                    record.getDuration(),
                    record.getPlannedDuration(),
                    record.getStatus(),
                    record.getStartedAt(),
                    record.getFinishedAt() != null ? record.getFinishedAt() : "");
        }
        writer.flush();
    }

    private String escapeCsv(String value) {
        if (value == null) return "";
        if (value.contains(",") || value.contains("\"") || value.contains("\n")) {
            return "\"" + value.replace("\"", "\"\"") + "\"";
        }
        return value;
    }
}
