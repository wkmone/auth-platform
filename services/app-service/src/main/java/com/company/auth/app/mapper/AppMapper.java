package com.company.auth.app.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.company.auth.app.entity.OAuth2ApplicationEntity;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.Select;

@Mapper
public interface AppMapper extends BaseMapper<OAuth2ApplicationEntity> {

    @Select("""
        SELECT * FROM oauth2_application
        WHERE (CAST(#{appName} AS VARCHAR) IS NULL OR app_name LIKE CONCAT('%', CAST(#{appName} AS VARCHAR), '%'))
        AND (CAST(#{status} AS VARCHAR) IS NULL OR status = CAST(#{status} AS VARCHAR))
        ORDER BY created_at DESC
    """)
    IPage<OAuth2ApplicationEntity> pageApps(Page<OAuth2ApplicationEntity> page,
                                             @Param("appName") String appName,
                                             @Param("status") String status);
}
