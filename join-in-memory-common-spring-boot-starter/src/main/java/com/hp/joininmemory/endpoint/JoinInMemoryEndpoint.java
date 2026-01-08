package com.hp.joininmemory.endpoint;

import com.hp.joininmemory.cache.JoinInMemoryCacheManager;
import lombok.RequiredArgsConstructor;
import org.springframework.boot.actuate.endpoint.annotation.Endpoint;
import org.springframework.boot.actuate.endpoint.annotation.ReadOperation;

import java.util.HashMap;
import java.util.Map;
import java.util.stream.Collectors;

/**
 * Actuator Endpoint
 *
 * @author <a href="mailto:max_verstrappon@outlook.com">HuPeng</a>
 */
@Endpoint(id = "join-in-memory")
@RequiredArgsConstructor
public class JoinInMemoryEndpoint {

    private final JoinInMemoryCacheManager cacheManager;

    @ReadOperation
    public Map<String, Object> info() {
        Map<String, Object> info = new HashMap<>();
        info.put("cacheSize", cacheManager.getCacheSize());
        info.put("cachedClasses", cacheManager.getCacheKeys().stream()
                .map(Class::getSimpleName)
                .collect(Collectors.toList()));
        return info;
    }
}
