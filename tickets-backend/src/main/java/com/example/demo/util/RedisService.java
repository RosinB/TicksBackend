package com.example.demo.util;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Service;

import java.util.Arrays;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.TimeUnit;
import java.util.stream.Collectors;

@Service
public class RedisService {

	@Autowired
	private RedisTemplate<String, Object> redisTemplate;
	 @Autowired
	 @Qualifier("setOperationsTemplate")  // 使用新名字注入
	 private RedisTemplate<String, String> setOperationsTemplate;
	@Autowired
	private ObjectMapper objectMapper; // Jackson 序列化工具

	// 保存數據到 Redis
	public void save(String key, Object value) {
		redisTemplate.opsForValue().set(key, value);
	}

	// 減少數值並返回減少後的結果
	public Long decrement(String key, Integer value) {
		return redisTemplate.opsForValue().increment(key, -value); // 傳遞負數
	}

	// 保存數據到 Redis，並設置過期時間
	public void saveWithExpire(String key, Object value, long timeout, TimeUnit unit) {
		redisTemplate.opsForValue().set(key, value, timeout, unit);
	}

	// 獲取數據（泛型支持）
	public <T> T get(String key, Class<T> clazz) {
		Object value = redisTemplate.opsForValue().get(key);
		if (value == null) {
			return null;
		}
		// 如果直接是正確類型，返回
		if (clazz.isInstance(value)) {
			return clazz.cast(value);
		}
		// 否則反序列化為指定類型
		return objectMapper.convertValue(value, clazz);
	}

	// 獲取泛型數據（解決 List<EventDto> 的問題）
	public <T> T get(String key, TypeReference<T> typeReference) {
		Object value = redisTemplate.opsForValue().get(key);
		if (value == null) {
			return null;
		}
		// 將數據反序列化為泛型類型
		return objectMapper.convertValue(value, typeReference);
	}

	// 刪除數據
	public void delete(String key) {
		redisTemplate.delete(key);
	}

	// 檢查鍵是否存在
	public boolean exists(String key) {
		return Boolean.TRUE.equals(redisTemplate.hasKey(key));
	}

	// 設置過期時間
	public void expire(String key, long timeout, TimeUnit unit) {
		redisTemplate.expire(key, timeout, unit);
	}

	public Long increment(String key, Integer value) {
		try {
			return redisTemplate.opsForValue().increment(key, value); // 傳遞正數
		} catch (Exception e) {
			throw new RuntimeException("回滾庫存操作失敗，Key: " + key + "，錯誤信息: " + e.getMessage(), e);
		}
	}

	public Set<String> keys(String pattern) {
		try {
			return redisTemplate.keys(pattern);
		} catch (Exception e) {
			throw new RuntimeException("獲取 Redis 鍵失敗，匹配模式: " + pattern, e);
		}
	}

	public List<Object> multiGet(Set<String> keys) {
		try {
			return redisTemplate.opsForValue().multiGet(keys);
		} catch (Exception e) {
			throw new RuntimeException("批量獲取 Redis 鍵值失敗，鍵集合: " + keys, e);
		}
	}

	// Hash 操作: 同時設置多個字段
	public void hashMultiSet(String key, Map<String, Object> map) {
		redisTemplate.opsForHash().putAll(key, map);
	}

	// Set 操作: 獲取集合所有成員
	public Set<String> sMembers(String key) {
        return setOperationsTemplate.opsForSet().members(key);
	}

	public void sAdd(String key, String... values) {
		redisTemplate.opsForSet().add(key, values);
	}

	// 從左側插入列表
	public void listLeftPush(String key, String value) {
		redisTemplate.opsForList().leftPush(key, value);
	}

	// 獲取列表指定範圍的值
	public List<String> listRange(String key, long start, long end) {
		return redisTemplate.opsForList().range(key, start, end).stream().map(Object::toString)
				.collect(Collectors.toList());
	}

	// 修剪列表
	public void listTrim(String key, long start, long end) {
		redisTemplate.opsForList().trim(key, start, end);
	}

	// 獲取列表長度
	public Long listSize(String key) {
		return redisTemplate.opsForList().size(key);
	}

	// 使用 stringRedisTemplate 進行 Set 操作
    public Long sadd(String key, String... values) {
        try {
            return setOperationsTemplate.opsForSet().add(key, values);
        } catch (Exception e) {
            throw new RuntimeException("向 Redis 集合添加元素失敗，Key: " + key + "，錯誤信息: " + e.getMessage(), e);
        }
    }

    // 移除操作也使用 stringRedisTemplate
    public Long srem(String key, String... values) {
        try {
            return setOperationsTemplate.opsForSet().remove(key, values);
        } catch (Exception e) {
            throw new RuntimeException("從 Redis 集合移除元素失敗，Key: " + key + "，錯誤信息: " + e.getMessage(), e);
        }
    }

}