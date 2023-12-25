package com.example.reggie_take_out;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.core.ValueOperations;
import org.springframework.web.client.RestTemplate;


@SpringBootTest
class ReggieTakeOutApplicationTests {

    @Autowired
    private RedisTemplate redisTemplate;
    @Test
    void contextLoads() {
        ValueOperations valueOperations = redisTemplate.opsForValue();
        valueOperations.set("name","张三");
        String name = (String)valueOperations.get("name");
        System.out.println(name);
    }

}
