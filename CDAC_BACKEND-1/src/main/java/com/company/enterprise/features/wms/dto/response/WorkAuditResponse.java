package com.company.enterprise.features.wms.dto.response;

import java.time.LocalDateTime;

import com.company.enterprise.core.auditing.AuditAction;

public record WorkAuditResponse(
        Long revisionNumber,
        LocalDateTime revisionDate,
        AuditAction revisionType,
        Long legacyRevision,
        WorkResponse work,
        String createdBy,
        String modifiedBy,
        Long userId,
        String userName,
        String ipAddress,
        String macAddress) {
}
