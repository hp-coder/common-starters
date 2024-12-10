package com.hp.excel.context;

import com.google.common.collect.Maps;

import java.util.Map;
import java.util.Optional;

import static com.hp.excel.constant.ExcelConstants.DROPDOWN_QUERY_PARAMS_ATTRIBUTE_KEY;
import static com.hp.excel.constant.ExcelConstants.FILENAME_ATTRIBUTE_KEY;

/**
 * @author hp
 */
public class ExcelContext {

    private static final ThreadLocal<Map<String, Object>> threadLocal = new ThreadLocal<>();

    @SuppressWarnings("unchecked")
    private static <T> Optional<T> value(String key) {
        return Optional.ofNullable(threadLocal.get()).map(any -> (T) any.get(key));
    }

    public static void setParameters(Map<String, Object> parameters) {
        Optional.ofNullable(threadLocal.get())
                .ifPresentOrElse(
                        map -> map.put(DROPDOWN_QUERY_PARAMS_ATTRIBUTE_KEY, parameters),
                        () -> {
                            final Map<String, Object> map = Maps.newConcurrentMap();
                            map.put(DROPDOWN_QUERY_PARAMS_ATTRIBUTE_KEY, parameters);
                            threadLocal.set(map);
                        });
    }

    public static void setFilename(String filename) {
        Optional.ofNullable(threadLocal.get())
                .ifPresentOrElse(
                        map -> map.put(FILENAME_ATTRIBUTE_KEY, filename),
                        () -> {
                            final Map<String, Object> map = Maps.newConcurrentMap();
                            map.put(FILENAME_ATTRIBUTE_KEY, filename);
                            threadLocal.set(map);
                        });
    }

    public static Optional<Map<String, Object>> getParameters() {
        return value(DROPDOWN_QUERY_PARAMS_ATTRIBUTE_KEY);
    }

    public static Optional<String> getFilename() {
        return value(FILENAME_ATTRIBUTE_KEY);
    }

    public static void clearParameters() {
        Optional.ofNullable(threadLocal.get()).ifPresent(i -> i.remove(DROPDOWN_QUERY_PARAMS_ATTRIBUTE_KEY));
    }

    public static void clearFilename() {
        Optional.ofNullable(threadLocal.get()).ifPresent(i -> i.remove(FILENAME_ATTRIBUTE_KEY));
    }

}
