package com.company.enterprise.core.auditing;

import java.io.IOException;
import java.security.Principal;

import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

@Component
public class AuditMetadataFilter extends OncePerRequestFilter {

    private static final String CLIENT_MAC_HEADER = "X-Client-Mac-Address";
    private static final String USERNAME_ATTRIBUTE = "preferred_username";

    @Override
    protected void doFilterInternal(
            HttpServletRequest request,
            HttpServletResponse response,
            FilterChain filterChain) throws ServletException, IOException {
        Principal principal = request.getUserPrincipal();
        String principalName = principal == null ? null : principal.getName();
        Object subject = request.getAttribute("sub");
        String userName = attributeValue(request, USERNAME_ATTRIBUTE);
        if (userName == null) {
            userName = attributeValue(request, "userName");
        }
        if (userName == null) {
            userName = principalName;
        }

        AuditContext.setUser(new AuditUser(
                parseUserId(subject == null ? principalName : subject.toString()),
                userName,
                request.getRemoteAddr(),
                request.getHeader(CLIENT_MAC_HEADER)));

        try {
            filterChain.doFilter(request, response);
        } finally {
            AuditContext.clear();
        }
    }

    private String attributeValue(HttpServletRequest request, String name) {
        Object value = request.getAttribute(name);
        return value instanceof String text && !text.isBlank() ? text : null;
    }

    private Long parseUserId(String principalName) {
        if (principalName == null) {
            return null;
        }

        try {
            return Long.valueOf(principalName);
        } catch (NumberFormatException exception) {
            return null;
        }
    }
}
