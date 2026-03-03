package com.pomodoro.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.pomodoro.entity.PomodoroRecord;
import org.apache.ibatis.annotations.Mapper;

@Mapper
public interface PomodoroRecordMapper extends BaseMapper<PomodoroRecord> {
}
