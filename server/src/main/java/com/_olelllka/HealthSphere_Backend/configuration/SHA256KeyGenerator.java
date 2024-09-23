package com._olelllka.HealthSphere_Backend.configuration;

import com._olelllka.HealthSphere_Backend.service.SHA256;
import org.springframework.cache.interceptor.KeyGenerator;
import org.springframework.stereotype.Component;

import java.lang.reflect.Method;
import java.util.Arrays;

@Component
public class SHA256KeyGenerator implements KeyGenerator {
    @Override
    public Object generate(Object target, Method method, Object... params) {
            Object obj = Arrays.stream(params).findFirst().orElseThrow(() -> new RuntimeException("No Arguments found."));
            String key = obj.toString();
            return SHA256.generateSha256Hash(key);
    }
}
