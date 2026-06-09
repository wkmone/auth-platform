package com.company.auth.user.mapper;
import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.company.auth.user.entity.PasswordHistoryEntity;
import org.apache.ibatis.annotations.*;
import java.util.List;
import java.util.UUID;

@Mapper
public interface PasswordHistoryMapper extends BaseMapper<PasswordHistoryEntity> {
    @Select("SELECT * FROM sys_password_history WHERE user_id = #{userId} ORDER BY created_at DESC LIMIT 5")
    List<PasswordHistoryEntity> findTop5ByUserId(@Param("userId") UUID userId);
}
