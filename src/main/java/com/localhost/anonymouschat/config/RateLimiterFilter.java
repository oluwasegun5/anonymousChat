package com.localhost.anonymouschat.config;

import io.github.bucket4j.Bandwidth;
import io.github.bucket4j.Bucket;
import io.github.bucket4j.Refill;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.core.Ordered;
import org.springframework.core.annotation.Order;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;
import java.time.Duration;
import java.util.Arrays;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

@Component
@Order(Ordered.HIGHEST_PRECEDENCE + 1)  // Ensure proper filter order
public class RateLimiterFilter extends OncePerRequestFilter {

    private final Map<String, Bucket> buckets = new ConcurrentHashMap<>();

    // Rate limits: 10 requests/minute for API, unlimited for Swagger
    private static final Bandwidth API_LIMIT = Bandwidth.classic(10,
            Refill.intervally(10, Duration.ofMinutes(1)));

    @Override
    protected void doFilterInternal(HttpServletRequest request,
                                    HttpServletResponse response,
                                    FilterChain filterChain)
            throws IOException, ServletException, ServletException {

        // Skip rate limiting for Swagger/whitelisted endpoints
        if (isWhitelisted(request)) {
            filterChain.doFilter(request, response);
            return;
        }

        String clientKey = getClientIP(request);
        Bucket bucket = buckets.computeIfAbsent(clientKey, k ->
                Bucket.builder().addLimit(API_LIMIT).build()
        );

        if (bucket.tryConsume(1)) {
            filterChain.doFilter(request, response);
        } else {
            response.sendError(429, "Too Many Requests - API Rate Limit Exceeded");
        }
    }

    private boolean isWhitelisted(HttpServletRequest request) {
        return Arrays.stream(WhiteList.AUTH_WHITELIST)
                .anyMatch(path -> request.getRequestURI().startsWith(path));
    }

    private String getClientIP(HttpServletRequest request) {
        String xfHeader = request.getHeader("X-Forwarded-For");
        if (xfHeader != null) {
            return xfHeader.split(",")[0];
        }
        return request.getRemoteAddr();
    }
}