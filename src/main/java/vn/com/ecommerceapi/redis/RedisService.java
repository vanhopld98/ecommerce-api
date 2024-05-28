package vn.com.ecommerceapi.redis;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Component;

import java.util.Date;
import java.util.concurrent.TimeUnit;

@Component
public class RedisService {

    private static final Logger LOGGER = LoggerFactory.getLogger(RedisService.class);

    @SuppressWarnings("rawtypes")
    @Autowired
    protected RedisTemplate redisTemplate;

    @SuppressWarnings("unchecked")
    public void set(String key, String value) {
        LOGGER.info("[REDIS][SET][Key: {}][Value: {}]", key, value);
        redisTemplate.opsForValue().set(key, value);
    }

    public String get(String key) {
        var data = redisTemplate.opsForValue().get(key);
        LOGGER.info("[REDIS][GET][Key: {}][Data: {}]", key, data);
        return data != null ? (String) data : null;
    }

    @SuppressWarnings("unchecked")
    public boolean deleteKey(String key) {
        LOGGER.info("[REDIS][DELETE][Key: {}]", key);
        return Boolean.TRUE.equals(redisTemplate.delete(key));
    }

    @SuppressWarnings("unchecked")
    public void set(String key, String value, Date expiredDate) {
        long diff = expiredDate.getTime() - new Date().getTime();
        redisTemplate.opsForValue().set(key, value, diff, TimeUnit.SECONDS);
    }

    @SuppressWarnings("unchecked")
    public void set(String key, String value, Long expired) {
        redisTemplate.opsForValue().set(key, value, expired, TimeUnit.SECONDS);
    }

}