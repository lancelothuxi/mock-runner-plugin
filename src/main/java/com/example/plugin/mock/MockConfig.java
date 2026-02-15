package com.example.plugin.mock;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Mock 配置类，存储方法的 Mock 规则
 */
public class MockConfig implements Serializable {
    private static final long serialVersionUID = 1L;

    // key: className.methodName, value: MockRule
    private Map<String, MockRule> mockRules = new HashMap<>();
    private List<MockMethodConfig> mockMethods = new ArrayList<>();

    public void addMockRule(String className, String methodName, MockRule rule) {
        String key = className + "." + methodName;
        mockRules.put(key, rule);
    }

    public MockRule getMockRule(String className, String methodName) {
        String key = className + "." + methodName;
        return mockRules.get(key);
    }

    public Map<String, MockRule> getAllRules() {
        return mockRules;
    }

    public void addMockMethod(MockMethodConfig methodConfig) {
        mockMethods.add(methodConfig);
    }

    public void removeMockMethod(String className, String methodName) {
        mockMethods.removeIf(m ->
            m.getClassName().equals(className) && m.getMethodName().equals(methodName)
        );
    }

    public List<MockMethodConfig> getMockMethods() {
        return mockMethods;
    }

    public void clearAll() {
        mockRules.clear();
        mockMethods.clear();
    }

    public static class MockRule implements Serializable {
        private static final long serialVersionUID = 1L;

        private String returnValue;
        private String returnType;
        private boolean enabled = true;

        public MockRule() {}

        public MockRule(String returnValue, String returnType) {
            this.returnValue = returnValue;
            this.returnType = returnType;
        }

        public String getReturnValue() {
            return returnValue;
        }

        public void setReturnValue(String returnValue) {
            this.returnValue = returnValue;
        }

        public String getReturnType() {
            return returnType;
        }

        public void setReturnType(String returnType) {
            this.returnType = returnType;
        }

        public boolean isEnabled() {
            return enabled;
        }

        public void setEnabled(boolean enabled) {
            this.enabled = enabled;
        }
    }
}
