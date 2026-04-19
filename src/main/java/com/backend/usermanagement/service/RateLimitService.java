package com.backend.usermanagement.service;

import com.backend.usermanagement.exception.TooManyRequestsException;
import io.github.bucket4j.Bandwidth;
import io.github.bucket4j.Bucket;
import io.github.bucket4j.Refill;
import org.springframework.stereotype.Service;

import java.time.Duration;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

@Service
public class RateLimitService {

    private final Map<String, Bucket> buckets = new ConcurrentHashMap<>();

    public void consumeTokenOrThrow(String key, int capacity, int refillTokens, Duration refillDuration, String message) {
        Bucket bucket = buckets.computeIfAbsent(key, ignored -> Bucket.builder()
                .addLimit(Bandwidth.classic(capacity, Refill.greedy(refillTokens, refillDuration)))
                .build());

        if (!bucket.tryConsume(1)) {
            throw new TooManyRequestsException(message);
        }
    }
}
