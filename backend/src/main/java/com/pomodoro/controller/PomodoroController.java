package com.pomodoro.controller;

import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.pomodoro.common.Result;
import com.pomodoro.dto.StartPomodoroRequest;
import com.pomodoro.entity.PomodoroRecord;
import com.pomodoro.service.PomodoroService;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.time.LocalDate;
import java.util.List;

@RestController
@RequestMapping("/api/pomodoros")
@RequiredArgsConstructor
public class PomodoroController {

    private final PomodoroService pomodoroService;

    @PostMapping("/start")
    public ResponseEntity<Result<PomodoroRecord>> start(@Valid @RequestBody StartPomodoroRequest request,
                                                         HttpServletRequest httpRequest) {
        Long userId = (Long) httpRequest.getAttribute("userId");
        return ResponseEntity.ok(pomodoroService.start(userId, request));
    }

    @PutMapping("/{id}/complete")
    public ResponseEntity<Result<PomodoroRecord>> complete(@PathVariable Long id,
                                                            HttpServletRequest request) {
        Long userId = (Long) request.getAttribute("userId");
        Result<PomodoroRecord> result = pomodoroService.complete(userId, id);
        if (!result.isSuccess()) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(result);
        }
        return ResponseEntity.ok(result);
    }

    @PutMapping("/{id}/interrupt")
    public ResponseEntity<Result<PomodoroRecord>> interrupt(@PathVariable Long id,
                                                             HttpServletRequest request) {
        Long userId = (Long) request.getAttribute("userId");
        Result<PomodoroRecord> result = pomodoroService.interrupt(userId, id);
        if (!result.isSuccess()) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(result);
        }
        return ResponseEntity.ok(result);
    }

    @PutMapping("/{id}/abandon")
    public ResponseEntity<Result<PomodoroRecord>> abandon(@PathVariable Long id,
                                                           HttpServletRequest request) {
        Long userId = (Long) request.getAttribute("userId");
        Result<PomodoroRecord> result = pomodoroService.abandon(userId, id);
        if (!result.isSuccess()) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(result);
        }
        return ResponseEntity.ok(result);
    }

    @GetMapping
    public ResponseEntity<Result<Page<PomodoroRecord>>> list(
            @RequestParam(defaultValue = "1") int page,
            @RequestParam(defaultValue = "20") int size,
            @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate startDate,
            @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate endDate,
            @RequestParam(required = false) Long tagId,
            HttpServletRequest request) {
        Long userId = (Long) request.getAttribute("userId");
        return ResponseEntity.ok(pomodoroService.list(userId, page, size, startDate, endDate, tagId));
    }

    @GetMapping("/today")
    public ResponseEntity<Result<List<PomodoroRecord>>> today(HttpServletRequest request) {
        Long userId = (Long) request.getAttribute("userId");
        return ResponseEntity.ok(pomodoroService.todayRecords(userId));
    }
}
