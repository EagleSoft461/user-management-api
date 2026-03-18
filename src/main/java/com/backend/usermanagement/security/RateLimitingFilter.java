package com.backend.usermanagement.security;

import com.fasterxml.jackson.databind.ObjectMapper;
import io.github.bucket4j.Bandwidth;
import io.github.bucket4j.Bucket;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;
import java.time.Duration;
import java.time.LocalDateTime;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

@Component
public class RateLimitingFilter extends OncePerRequestFilter {

    // Her IP için ayrı bucket saklanır (thread-safe map)
    private final Map<String, Bucket> loginBuckets = new ConcurrentHashMap<>();
    private final Map<String, Bucket> registerBuckets = new ConcurrentHashMap<>();
    private final Map<String, Bucket> forgotPasswordBuckets = new ConcurrentHashMap<>();
    private final Map<String, Bucket> generalBuckets = new ConcurrentHashMap<>();

    private final ObjectMapper objectMapper = new ObjectMapper();

    @Override
    protected void doFilterInternal(HttpServletRequest request,
                                    HttpServletResponse response,
                                    FilterChain filterChain) throws ServletException, IOException {

        String ip = getClientIp(request);
        String path = request.getRequestURI();
        String method = request.getMethod();

        // Sadece POST isteklerine ve ilgili path'lere uygula
        Bucket bucket = resolveBucket(ip, path, method);

        if (bucket != null && !bucket.tryConsume(1)) {
            // Token yok → 429 Too Many Requests
            sendRateLimitResponse(response, path);
            return;
        }

        filterChain.doFilter(request, response);
    }

    private Bucket resolveBucket(String ip, String path, String method) {
        if ("POST".equals(method)) {
            if (path.equals("/auth/login")) {
                // Login: dakikada 5 istek
                return loginBuckets.computeIfAbsent(ip, k -> createBucket(5, Duration.ofMinutes(1)));
            }
            if (path.equals("/auth/register")) {
                // Register: dakikada 3 istek
                return registerBuckets.computeIfAbsent(ip, k -> createBucket(3, Duration.ofMinutes(1)));
            }
            if (path.equals("/auth/forgot-password")) {
                // Forgot password: dakikada 3 istek
                return forgotPasswordBuckets.computeIfAbsent(ip, k -> createBucket(3, Duration.ofMinutes(1)));
            }
        }
        // Genel API: dakikada 30 istek
        if (path.startsWith("/api/")) {
            return generalBuckets.computeIfAbsent(ip, k -> createBucket(30, Duration.ofMinutes(1)));
        }
        return null; // Rate limit uygulanmaz
    }

    // Bucket oluştur: capacity kadar token, duration sürede yenilenir
    private Bucket createBucket(int capacity, Duration duration) {
        Bandwidth limit = Bandwidth.builder()
                .capacity(capacity)
                .refillGreedy(capacity, duration)
                .build();
        return Bucket.builder().addLimit(limit).build();
    }

    // Gerçek IP adresini al (proxy arkasında da çalışır)
    private String getClientIp(HttpServletRequest request) {
        String xForwardedFor = request.getHeader("X-Forwarded-For");
        if (xForwardedFor != null && !xForwardedFor.isEmpty()) {
            return xForwardedFor.split(",")[0].trim();
        }
        return request.getRemoteAddr();
    }

    private void sendRateLimitResponse(HttpServletResponse response, String path) throws IOException {
        response.setStatus(HttpStatus.TOO_MANY_REQUESTS.value());
        response.setContentType(MediaType.APPLICATION_JSON_VALUE);

        Map<String, Object> body = Map.of(
                "timestamp", LocalDateTime.now().toString(),
                "status", 429,
                "error", "Too Many Requests",
                "message", "Rate limit exceeded. Please try again later.",
                "path", path
        );

        response.getWriter().write(objectMapper.writeValueAsString(body));
    }
}
