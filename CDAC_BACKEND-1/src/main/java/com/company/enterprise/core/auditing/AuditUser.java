package com.company.enterprise.core.auditing;

public record AuditUser(Long userId, String userName, String ipAddress, String macAddress) {
}
