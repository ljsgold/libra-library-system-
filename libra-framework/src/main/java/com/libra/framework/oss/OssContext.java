package com.libra.framework.oss;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

import java.util.Map;

@Component
@RequiredArgsConstructor
public class OssContext {

    private final Map<String, OssStrategy> strategies;

    public OssStrategy getStrategy(String type) {
        return strategies.get(type);
    }
}
