package com.pomodoro.entity;

import com.baomidou.mybatisplus.annotation.FieldFill;
import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import com.fasterxml.jackson.annotation.JsonIgnore;
import lombok.Data;
import lombok.ToString;

import java.time.LocalDateTime;

@Data
@TableName("users")
public class User {

    @TableId(type = IdType.AUTO)
    private Long id;

    private String username;

    private String email;

    @ToString.Exclude
    @JsonIgnore
    private String passwordHash;

    private String avatarUrl;

    private Integer dailyGoal;

    @TableField(fill = FieldFill.INSERT)
    private LocalDateTime createdAt;

    @TableField(fill = FieldFill.INSERT_UPDATE)
    private LocalDateTime updatedAt;
}
