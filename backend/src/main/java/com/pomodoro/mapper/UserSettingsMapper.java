package com.pomodoro.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.pomodoro.entity.UserSettings;
import org.apache.ibatis.annotations.Mapper;

@Mapper
public interface UserSettingsMapper extends BaseMapper<UserSettings> {
}
