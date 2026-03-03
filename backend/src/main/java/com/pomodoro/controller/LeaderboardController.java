package com.pomodoro.controller;

import com.pomodoro.common.Result;
import com.pomodoro.service.LeaderboardService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/leaderboard")
@RequiredArgsConstructor
public class LeaderboardController {

    private final LeaderboardService leaderboardService;

    @GetMapping("/daily")
    public ResponseEntity<Result<List<Map<String, Object>>>> daily() {
        return ResponseEntity.ok(leaderboardService.daily());
    }

    @GetMapping("/weekly")
    public ResponseEntity<Result<List<Map<String, Object>>>> weekly() {
        return ResponseEntity.ok(leaderboardService.weekly());
    }
}
