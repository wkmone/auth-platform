package com.company.auth.user.mapper;
import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.company.auth.user.entity.LoginLogEntity;
import org.apache.ibatis.annotations.Mapper;

@Mapper
public interface LoginLogMapper extends BaseMapper<LoginLogEntity> {
}
