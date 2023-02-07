package com.luban.excel.example;

import com.google.common.collect.Lists;
import lombok.extern.slf4j.Slf4j;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.data.redis.core.script.DefaultRedisScript;
import org.springframework.data.redis.core.script.RedisScript;

import java.util.concurrent.CountDownLatch;
import java.util.concurrent.Executor;
import java.util.concurrent.Executors;
import java.util.concurrent.atomic.AtomicInteger;

/**
 * @author HP 2023/1/13
 */
@Slf4j
@SpringBootTest(classes = ExcelExampleApplication.class)
public class TokenBucketTest {

    public static final String LUA = "local ratelimit_info = redis.pcall('HMGET',KEYS[1],'last_time','current_token')\n" +
            "local last_time\n" +
            "if ratelimit_info[1] then\n" +
            "  last_time = tonumber(ratelimit_info[1])\n" +
            "  end\n" +
            "local current_token = 0\n" +
            "if ratelimit_info[2] then\n" +
            "   current_token = tonumber(ratelimit_info[2])\n" +
            "   end\n" +
            "local max_token = tonumber(ARGV[1])\n" +
            "-- 令牌生成速率是每秒多少个\n" +
            "local token_rate = tonumber(ARGV[2])\n" +
            "-- 由于当前请求时间是通过传入的方式实现，因此务必保证所有节点的时间是同步的\n" +
            "local current_time = tonumber(ARGV[3])\n" +
            "-- 计算得出生成一个令牌需要多少时间，为了下面计算两次请求之间需要生成的令牌树作铺垫\n" +
            "local reverse_time = 1000/token_rate\n" +
            "if last_time == nil then\n" +
            "  -- 初始化置为1，同时消耗未来的时间\n" +
            "  current_token = current_token + 1\n" +
            "  last_time = current_time + reverse_time/2;\n" +
            "end\n" +
            "if (current_time > last_time) then\n" +
            "  -- 计算两次请求的时间差，毫秒\n" +
            "  local past_time = current_time-last_time\n" +
            "  -- 计算这段时间内需要生成的令牌数\n" +
            "  local reverse_token = math.floor(past_time/reverse_time)\n" +
            "  current_token = current_token+reverse_token\n" +
            "  last_time = reverse_time*reverse_token+last_time\n" +
            "  if current_token>max_token then\n" +
            "    current_token = max_token\n" +
            "  end\n" +
            "end\n" +
            "local result = 0\n" +
            "if(current_token>0) then\n" +
            "  result = 1\n" +
            "  current_token = current_token-1\n" +
            "end\n" +
            "redis.call('HMSET',KEYS[1],'last_time',last_time,'current_token',current_token)\n" +
            "local expireTime = math.ceil(reverse_time*(max_token-current_token))\n" +
            "if(current_time > last_time) then\n" +
            "  expireTime = expireTime + (current_time-last_time)\n" +
            "end\n" +
            "-- 过期时间设为令牌桶生成至满所需要的时间\n" +
            "redis.call('pexpire',KEYS[1],expireTime)\n" +
            "return result\n";

    private final static String LUA_SCRIPT = "local ratelimit_info = redis.pcall('HMGET',KEYS[1],'last_time','current_token')\n" +
            "local last_time = ratelimit_info[1]\n" +
            "local current_token\n" +
            "if ratelimit_info[2] then\n" +
            "   current_token = tonumber(ratelimit_info[2])\n" +
            "end\n" +
            "local max_token = tonumber(ARGV[1])\n" + //100
            "local token_rate = tonumber(ARGV[2])\n" + //100/s
            "local current_time = tonumber(ARGV[3])\n" + //当前时间戳
            "local reverse_time = 1000/token_rate\n" + // 1000/100 = 10
            "if current_token == nil then\n" + //如果当前current_token为null
            "  current_token = max_token\n" + //current_token = 100
            "  last_time = current_time\n" + // last_time = 当前时间戳
            "else\n" +
            "  local past_time = current_time-last_time\n" + //时间戳 - 上次的时间 = 过去了多久
            "  local reverse_token = math.floor(past_time/reverse_time)\n" + // 这段时间要生成多少token
            "  current_token = current_token+reverse_token\n" + //累加当前token
            "  last_time = reverse_time*reverse_token+last_time\n" +
            "  if current_token>max_token then\n" + //修正token数量
            "    current_token = max_token\n" +
            "  end\n" +
            "end\n" +
            "local result = 0\n" + //取token
            "if(current_token>0) then\n" +
            "  result = 1\n" +
            "  current_token = current_token-1\n" + //token减1
            "end\n" +
            "redis.call('HMSET',KEYS[1],'last_time',last_time,'current_token',current_token)\n" +
            "redis.call('pexpire',KEYS[1],1000000)\n" +
            "return result";

    @Autowired
    private StringRedisTemplate redisTemplate;

    @Test
    public void test_create_and_consume_from_token_bucket() throws InterruptedException {
        String key = "test_rateLimit_key";
        int max = 500;  //令牌桶大小
        int rate = 8; //令牌每秒恢复速度
        AtomicInteger successCount = new AtomicInteger(0);
        Executor executor = Executors.newFixedThreadPool(10);
        CountDownLatch countDownLatch = new CountDownLatch(30);
        for (int i = 0; i < 30; i++) {
            executor.execute(() -> {
                RedisScript<Long> redisScript = new DefaultRedisScript<>(LUA_SCRIPT, Long.class);
                Long result = redisTemplate.execute(redisScript, Lists.newArrayList(key), Integer.toString(max), Integer.toString(rate), Long.toString(System.currentTimeMillis()));
                boolean isAllow = false;
                if (result != null && 1 == result) {
                    isAllow = true;
                    successCount.addAndGet(1);
                }
                log.info(Boolean.toString(isAllow));
                countDownLatch.countDown();
            });
        }
        countDownLatch.await();
        log.info("请求成功{}次", successCount.get());
    }
}
