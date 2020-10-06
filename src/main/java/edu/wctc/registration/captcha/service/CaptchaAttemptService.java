package edu.wctc.registration.captcha.service;

import com.google.common.cache.CacheBuilder;
import com.google.common.cache.CacheLoader;
import com.google.common.cache.LoadingCache;
import org.springframework.stereotype.Service;

import java.util.concurrent.TimeUnit;

@Service
public class CaptchaAttemptService {
    private final int MAX_ATTEMPTS = 4;
    /**
     * This is neat! https://github.com/google/guava/wiki/CachesExplained
     */
    private LoadingCache<String, Integer> attemptsCache;

    public CaptchaAttemptService() {
        // Uses the IP as the key, number of failed Captcha attempts
        // as the value. Entries expire after 4 hours.
        attemptsCache = CacheBuilder.newBuilder()
                .expireAfterWrite(4, TimeUnit.HOURS)
                .build(new CacheLoader<>() {
                    @Override
                    public Integer load(String ipAddress) {
                        return 0;
                    }
                });
    }

    public boolean isBlocked(String ipAddress) {
        return attemptsCache.getUnchecked(ipAddress) >= MAX_ATTEMPTS;
    }

    public void reCaptchaFailed(String ipAddress) {
        // Add to the failed attempt counter for this IP
        int attempts = attemptsCache.getUnchecked(ipAddress);
        attempts++;
        attemptsCache.put(ipAddress, attempts);
    }

    public void reCaptchaSucceeded(String ipAddress) {
        attemptsCache.invalidate(ipAddress);
    }
}
