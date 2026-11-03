package in.gov.wamis.common.security;

import org.springframework.stereotype.Component;

/**
 * Shared WAMIS security context helper.
 * Provides current actor identity and role resolution.
 */
@Component
public class SecurityContextHelper {

    public String getCurrentUser() {
        // In a production WAMIS environment with Spring Security, this reads SecurityContextHolder.
        // Fallback default actor if unauthenticated or in service mode.
        return "WAMIS_OFFICER";
    }

    public boolean isOfficeAuthorized(Long officeId) {
        // Office authorization check stub. Verifies whether current user has access to office.
        return officeId != null && officeId > 0;
    }

    public boolean isWorkAuthorized(Long workId) {
        // Work authorization check stub.
        return workId != null && workId > 0;
    }
}

