package com.example.linkup.service;

import lombok.*;
import lombok.experimental.FieldDefaults;

import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Set;
import java.util.concurrent.TimeUnit;

@Service
@RequiredArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
public class OnlineUserService {
    RedisTemplate<String, Object> redisTemplate;

    public void setOnlineUser(int userId) {
        redisTemplate.opsForValue().set("online:" + userId, true, 15, TimeUnit.SECONDS);
    }

    public void setOfflineUser(int userId) {
        redisTemplate.delete("online:" + userId);
    }

    public boolean isUserOnline(int userId) {
        return redisTemplate.hasKey("online:" + userId);
    }

    public List<Integer> getOnlineUserIds() {
        Set<String> keys = redisTemplate.keys("online:*");
        if (keys.isEmpty()) return List.of();

        return keys.stream()
                .map(key -> key.replace("online:", ""))
                .map(Integer::valueOf)
                .toList();
    }
}
