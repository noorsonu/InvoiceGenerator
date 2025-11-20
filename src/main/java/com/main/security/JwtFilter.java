package com.main.security;

import java.io.IOException;

import com.main.servicesImpls.JwtAdminDetails;
import io.jsonwebtoken.ExpiredJwtException;
import io.jsonwebtoken.JwtException;
import io.jsonwebtoken.MalformedJwtException;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.web.authentication.WebAuthenticationDetailsSource;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

@Component
@Slf4j
public class JwtFilter extends OncePerRequestFilter {

    private final JwtHelper jwtHelper;
    private final JwtAdminDetails userDetails;
    private final TokenBlackList tokenBlackList;

    public JwtFilter(JwtHelper jwtHelper, JwtAdminDetails userDetails, TokenBlackList tokenBlackList) {
        this.jwtHelper = jwtHelper;
        this.userDetails = userDetails;
        this.tokenBlackList = tokenBlackList;
    }

    @Override
    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain filterChain)
            throws ServletException, IOException {

        String requestUri = request.getRequestURI();

        if (requestUri.startsWith("/api/admin/register") || requestUri.startsWith("/api/admin/login")
                || requestUri.startsWith("/api/admin/refresh") || requestUri.startsWith("/v3/api-docs")
                || requestUri.startsWith("/swagger-ui")) {
            filterChain.doFilter(request, response);
            return;
        }

        String reqestHeader = request.getHeader("Authorization");
        String username = null;
        String token = null;

        if (reqestHeader != null && reqestHeader.startsWith("Bearer ")) {
            token = reqestHeader.substring(7);
            if (tokenBlackList.isBlacklisted(token)) {
                log.info("Blacklisted token rejected");
                filterChain.doFilter(request, response);
                return;
            }
            try {
                username = jwtHelper.getUsernameFromToken(token);
            } catch (IllegalArgumentException e) {
                log.error("Illegal Argument while fetching the username!!", e);
            } catch (ExpiredJwtException e) {
                log.error("Given JWT token is expired!!", e);
            } catch (MalformedJwtException e) {
                log.error("The token is malformed or has been altered!!", e);
            } catch (Exception e) {
                log.error("Token validation failed due to an unexpected error", e);
            }
        }

        if (username != null && SecurityContextHolder.getContext().getAuthentication() == null) {
            try {
                UserDetails userDetails = this.userDetails.loadUserByUsername(username);
                boolean validToken = this.jwtHelper.validateToken(token, userDetails);

                if (validToken) {
                    UsernamePasswordAuthenticationToken authentication = new UsernamePasswordAuthenticationToken(
                            userDetails, null, userDetails.getAuthorities());
                    authentication.setDetails(new WebAuthenticationDetailsSource().buildDetails(request));
                    SecurityContextHolder.getContext().setAuthentication(authentication);
                } else {
                    log.info("Token validation failed!!");
                }
            } catch (UsernameNotFoundException e) {
                log.error("User not found: {}", e.getMessage());
            } catch (JwtException e) {
                log.error("JWT validation failed: {}", e.getMessage());
            } catch (Exception e) {
                log.error("An unexpected error occurred during authentication", e);
            }
        }

        filterChain.doFilter(request, response);
    }
}
