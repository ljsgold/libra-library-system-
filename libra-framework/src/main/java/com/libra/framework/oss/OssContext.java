package com.libra.framework.oss;

import org.springframework.stereotype.Component;
import org.springframework.beans.factory.annotation.Autowired;
import java.util.Map;

import java.util.Map;
import java.util.HashMap;

@Component
public class OssContext {

    @Autowired
    private Map<String, OssStrategy> strategies;

    public OssStrategy getStrategy(String type) {
        return strategies != null ? strategies.get(type) : null;
    }
}
