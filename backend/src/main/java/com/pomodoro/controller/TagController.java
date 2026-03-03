package com.pomodoro.controller;

import com.pomodoro.common.Result;
import com.pomodoro.dto.CreateTagRequest;
import com.pomodoro.dto.UpdateTagRequest;
import com.pomodoro.entity.Tag;
import com.pomodoro.service.TagService;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping("/api/tags")
@RequiredArgsConstructor
public class TagController {

    private final TagService tagService;

    @GetMapping
    public ResponseEntity<Result<List<Tag>>> list(HttpServletRequest request) {
        Long userId = (Long) request.getAttribute("userId");
        return ResponseEntity.ok(tagService.list(userId));
    }

    @PostMapping
    public ResponseEntity<Result<Tag>> create(@Valid @RequestBody CreateTagRequest tagRequest,
                                               HttpServletRequest request) {
        Long userId = (Long) request.getAttribute("userId");
        return ResponseEntity.status(HttpStatus.CREATED).body(tagService.create(userId, tagRequest));
    }

    @PutMapping("/{id}")
    public ResponseEntity<Result<Tag>> update(@PathVariable Long id,
                                               @Valid @RequestBody UpdateTagRequest tagRequest,
                                               HttpServletRequest request) {
        Long userId = (Long) request.getAttribute("userId");
        Result<Tag> result = tagService.update(userId, id, tagRequest);
        if (!result.isSuccess()) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(result);
        }
        return ResponseEntity.ok(result);
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Result<Void>> delete(@PathVariable Long id,
                                                HttpServletRequest request) {
        Long userId = (Long) request.getAttribute("userId");
        Result<Void> result = tagService.delete(userId, id);
        if (!result.isSuccess()) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(result);
        }
        return ResponseEntity.ok(result);
    }
}
