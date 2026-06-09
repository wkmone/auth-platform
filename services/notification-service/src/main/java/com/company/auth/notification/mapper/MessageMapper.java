package com.company.auth.notification.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.company.auth.notification.entity.MessageEntity;
import org.apache.ibatis.annotations.Mapper;

@Mapper
public interface MessageMapper extends BaseMapper<MessageEntity> {
}
