package org.example.client_processing.security;

import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import org.example.client_processing.service.ClientBlockingService;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;


@Component
@RequiredArgsConstructor
public class BlockedClientFilter extends OncePerRequestFilter {

    private final ClientBlockingService clientBlockingService;

    @Override
    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, 
                                  FilterChain filterChain) throws ServletException, IOException {
        
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        
        if (authentication != null && authentication.isAuthenticated()) {
            String username = authentication.getName();

            String clientId = extractClientIdFromRequest(request, username);
            
            if (clientId != null && clientBlockingService.isClientBlocked(clientId)) {
                response.setStatus(HttpServletResponse.SC_FORBIDDEN);
                response.getWriter().write("{\"error\":\"Access denied. Your account has been blocked.\",\"status\":403,\"message\":\"Please contact support for assistance\"}");
                response.setContentType("application/json");
                return;
            }
        }
        
        filterChain.doFilter(request, response);
    }

    private String extractClientIdFromRequest(HttpServletRequest request, String username) {
        String clientIdFromHeader = request.getHeader("X-Client-ID");
        if (clientIdFromHeader != null) {
            return clientIdFromHeader;
        }
        
        String clientIdFromParam = request.getParameter("clientId");
        if (clientIdFromParam != null) {
            return clientIdFromParam;
        }
        
        String path = request.getRequestURI();
        if (path.contains("/clients/")) {
            String[] pathParts = path.split("/");
            for (int i = 0; i < pathParts.length - 1; i++) {
                if ("clients".equals(pathParts[i])) {
                    return pathParts[i + 1];
                }
            }
        }
        
        return username;
    }
}
