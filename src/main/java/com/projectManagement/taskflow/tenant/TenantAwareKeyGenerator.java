package com.projectManagement.taskflow.tenant;

import org.springframework.cache.interceptor.KeyGenerator;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Component;

import java.lang.reflect.Method;
import java.util.Arrays;
import java.util.stream.Collectors;

@Component("tenantKeyGenerator")
public class TenantAwareKeyGenerator implements KeyGenerator {

    @Override
    public Object generate(Object target, Method method, Object... params) {
        String schema = TenantContext.getTenant();

        String paramKey = Arrays.stream(params)
                .map(p -> {
                    if (p == null) return "null";

                    if (p instanceof Pageable pageable) {
                        return "page=" + pageable.getPageNumber() +
                                "&size=" + pageable.getPageSize();
                    }

                    return p.toString();
                })
                .collect(Collectors.joining(":"));

        return schema + ":" + method.getName() + ":" + paramKey;
    }
}
