package com.company.auth.notification.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.company.auth.notification.entity.TemplateEntity;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.Select;

@Mapper
public interface TemplateMapper extends BaseMapper<TemplateEntity> {

    @Select("SELECT * FROM notification_template WHERE code = #{code} AND enabled = true")
    TemplateEntity findByCode(@Param("code") String code);
}
