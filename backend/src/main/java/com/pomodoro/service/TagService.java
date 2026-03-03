package com.pomodoro.service;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.pomodoro.common.Result;
import com.pomodoro.dto.CreateTagRequest;
import com.pomodoro.dto.UpdateTagRequest;
import com.pomodoro.entity.Tag;
import com.pomodoro.mapper.TagMapper;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
public class TagService {

    private final TagMapper tagMapper;

    public Result<List<Tag>> list(Long userId) {
        List<Tag> tags = tagMapper.selectList(
                new LambdaQueryWrapper<Tag>()
                        .eq(Tag::getUserId, userId)
                        .orderByAsc(Tag::getCreatedAt));
        return Result.ok(tags);
    }

    public Result<Tag> create(Long userId, CreateTagRequest request) {
        Tag tag = new Tag();
        tag.setUserId(userId);
        tag.setName(request.getName());
        tag.setColor(request.getColor());
        tagMapper.insert(tag);
        return Result.ok(tag);
    }

    public Result<Tag> update(Long userId, Long id, UpdateTagRequest request) {
        Tag tag = tagMapper.selectById(id);
        if (tag == null || !tag.getUserId().equals(userId)) {
            return Result.error("Tag not found");
        }
        tag.setName(request.getName());
        tag.setColor(request.getColor());
        tagMapper.updateById(tag);
        return Result.ok(tag);
    }

    public Result<Void> delete(Long userId, Long id) {
        Tag tag = tagMapper.selectById(id);
        if (tag == null || !tag.getUserId().equals(userId)) {
            return Result.error("Tag not found");
        }
        tagMapper.deleteById(id);
        return Result.ok();
    }
}
