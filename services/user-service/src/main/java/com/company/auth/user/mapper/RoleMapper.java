package com.company.auth.user.mapper;
import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.company.auth.user.entity.RoleEntity;
import org.apache.ibatis.annotations.Mapper;

@Mapper
public interface RoleMapper extends BaseMapper<RoleEntity> {
}
