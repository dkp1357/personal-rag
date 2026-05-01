package rag.backend.configuration;

import java.time.Duration;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import io.github.resilience4j.ratelimiter.RateLimiter;
import io.github.resilience4j.ratelimiter.RateLimiterConfig;
import io.github.resilience4j.ratelimiter.RateLimiterRegistry;

@Configuration
public class RateLimitingConfig {

    @Value("${rate-limiter.limit-for-period}")
    private int limitForPeriod;

    @Value("${rate-limiter.limit-refresh-period-seconds}")
    private int limitRefreshPeriodSeconds;

    @Value("${rate-limiter.timeout-duration-ms}")
    private int timeoutDurationMs;

    @Bean
    public RateLimiterRegistry rateLimiterRegistry() {
        RateLimiterConfig config = RateLimiterConfig.custom()
                .limitForPeriod(limitForPeriod)
                .limitRefreshPeriod(Duration.ofSeconds(limitRefreshPeriodSeconds))
                .timeoutDuration(Duration.ofMillis(timeoutDurationMs))
                .build();

        return RateLimiterRegistry.of(config);
    }

    @Bean
    public RateLimiter globalRateLimiter(RateLimiterRegistry registry) {
        return registry.rateLimiter("global");
    }
}
