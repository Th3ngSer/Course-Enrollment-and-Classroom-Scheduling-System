package com.couse_enrollment_and_class_scheduling.config;

import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.web.authentication.AuthenticationSuccessHandler;
import org.springframework.stereotype.Component;

import java.io.IOException;

@Component
public class CustomLoginSuccessHandler implements AuthenticationSuccessHandler {

    @Override
    public void onAuthenticationSuccess(
            HttpServletRequest request,
            HttpServletResponse response,
            Authentication authentication)
            throws IOException, ServletException {

        // Allow user to select a desired role during login via a form parameter named "role".
        // If provided and the authenticated user has that role, redirect there.
        String requestedRole = request.getParameter("role");

        if (requestedRole != null && !requestedRole.isBlank()) {
            boolean hasRequestedRole = false;
            for (GrantedAuthority authority : authentication.getAuthorities()) {
                if (authority.getAuthority().equals(requestedRole)) {
                    hasRequestedRole = true;
                    break;
                }
            }

            if (hasRequestedRole) {
                if (requestedRole.equals("ROLE_ADMIN")) {
                    response.sendRedirect("/admin/dashboard");
                    return;
                } else if (requestedRole.equals("ROLE_LECTURER")) {
                    response.sendRedirect("/lecturer/dashboard");
                    return;
                } else if (requestedRole.equals("ROLE_STUDENT")) {
                    response.sendRedirect("/student/dashboard");
                    return;
                }
            } else {
                // Requested role not permitted for this user; proceed to default and add flag
                // so UI can show a helpful message if desired.
                // Fall through to default behavior below but append a query flag.
            }
        }

        // Default behavior: choose the highest-priority role available
        String redirectUrl = "/student/dashboard";

        for (GrantedAuthority authority : authentication.getAuthorities()) {
            if (authority.getAuthority().equals("ROLE_ADMIN")) {
                redirectUrl = "/admin/dashboard";
                break;
            } else if (authority.getAuthority().equals("ROLE_LECTURER")) {
                redirectUrl = "/lecturer/dashboard";
                break;
            }
        }

        // If a role was requested but not available, append a flag to the redirect URL
        if (requestedRole != null && !requestedRole.isBlank()) {
            // ensure query string formatting
            if (redirectUrl.contains("?")) {
                redirectUrl += "&roleNotAllowed=1";
            } else {
                redirectUrl += "?roleNotAllowed=1";
            }
        }

        response.sendRedirect(redirectUrl);
    }
}
