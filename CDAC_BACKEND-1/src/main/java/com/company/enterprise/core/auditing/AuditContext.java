package com.company.enterprise.core.auditing;

public final class AuditContext {

    private static final ThreadLocal<AuditUser> CURRENT_USER = new ThreadLocal<>();
    private static final ThreadLocal<AuditAction> CURRENT_ACTION = new ThreadLocal<>();

    private AuditContext() {
    }

    public static void setUser(AuditUser user) {
        CURRENT_USER.set(user);
    }

    public static AuditUser getUser() {
        return CURRENT_USER.get();
    }

    public static void setAction(AuditAction action) {
        CURRENT_ACTION.set(action);
    }

    public static AuditAction getAction() {
        return CURRENT_ACTION.get();
    }

    public static void clearAction() {
        CURRENT_ACTION.remove();
    }

    public static void clear() {
        CURRENT_USER.remove();
        CURRENT_ACTION.remove();
    }
}
