package com.company.auth.audit.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.company.auth.audit.entity.AuditEventEntity;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.Select;

@Mapper
public interface AuditEventMapper extends BaseMapper<AuditEventEntity> {

    @Select("""
        SELECT * FROM audit_event
        WHERE (:eventType IS NULL OR event_type = CAST(:eventType AS VARCHAR))
        AND (:principalName IS NULL OR principal_name LIKE CONCAT('%', CAST(:principalName AS VARCHAR), '%'))
        AND (:result IS NULL OR result = CAST(:result AS VARCHAR))
        ORDER BY created_at DESC
    """)
    IPage<AuditEventEntity> pageEvents(Page<AuditEventEntity> page,
                                        @Param("eventType") String eventType,
                                        @Param("principalName") String principalName,
                                        @Param("result") String result);
}
