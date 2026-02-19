package io.github.lancelothuxi.idea.plugin.mock.mock;

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
        // 检查是否已存在相同的方法配置
        String methodKey = methodConfig.getClassName() + "." + methodConfig.getMethodName() + methodConfig.getSignature();
        
        // 移除已存在的相同配置
        mockMethods.removeIf(existing -> {
            String existingKey = existing.getClassName() + "." + existing.getMethodName() + existing.getSignature();
            return existingKey.equals(methodKey);
        });
        
        // 添加新配置
        mockMethods.add(methodConfig);

        // 同时添加到 mockRules，供 Agent 使用
        // 使用配置中的 returnType，如果没有则推断
        String returnType = methodConfig.getReturnType();
        if (returnType == null || returnType.isEmpty()) {
            returnType = inferReturnType(methodConfig.getReturnValue());
        }
        MockRule rule = new MockRule(
            methodConfig.getReturnValue(),
            returnType,
            methodConfig.isThrowException(),
            methodConfig.getExceptionType(),
            methodConfig.getExceptionMessage()
        );
        rule.setEnabled(methodConfig.isEnabled());
        addMockRule(methodConfig.getClassName(), methodConfig.getMethodName(), rule);
    }
    
    /**
     * 从 mockMethods 重建 mockRules
     * 用于从持久化状态加载后同步数据
     */
    public void rebuildMockRules() {
        mockRules.clear();
        for (MockMethodConfig methodConfig : mockMethods) {
            // 使用配置中的 returnType，如果没有则推断
            String returnType = methodConfig.getReturnType();
            if (returnType == null || returnType.isEmpty()) {
                returnType = inferReturnType(methodConfig.getReturnValue());
            }
            System.out.println("[MockConfig] rebuildMockRules: " + methodConfig.getClassName() + "." + methodConfig.getMethodName() + " -> returnType: " + returnType);
            MockRule rule = new MockRule(
                methodConfig.getReturnValue(),
                returnType,
                methodConfig.isThrowException(),
                methodConfig.getExceptionType(),
                methodConfig.getExceptionMessage()
            );
            rule.setEnabled(methodConfig.isEnabled());
            addMockRule(methodConfig.getClassName(), methodConfig.getMethodName(), rule);
        }
    }
    
    private String inferReturnType(String returnValue) {
        if (returnValue == null || returnValue.isEmpty()) {
            return "java.lang.Object";
        }
        if (returnValue.equals("[]") || returnValue.startsWith("[")) {
            return "java.util.List";
        }
        if (returnValue.equals("true") || returnValue.equals("false")) {
            return "boolean";
        }
        try {
            Integer.parseInt(returnValue);
            return "int";
        } catch (NumberFormatException e) {
            // 不是整数
        }
        return "java.lang.String";
    }

    public void removeMockMethod(String className, String methodName, String signature) {
        mockMethods.removeIf(m -> {
            String methodKey = m.getClassName() + "." + m.getMethodName() + m.getSignature();
            String targetKey = className + "." + methodName + signature;
            return methodKey.equals(targetKey);
        });
        
        // 同时从 mockRules 中删除
        String key = className + "." + methodName;
        mockRules.remove(key);
    }
    
    // 保留原有的方法以兼容现有代码
    public void removeMockMethod(String className, String methodName) {
        removeMockMethod(className, methodName, "");
    }

    public List<MockMethodConfig> getMockMethods() {
        return mockMethods;
    }
    
    /**
     * 检查是否已存在相同的mock配置
     */
    public boolean hasMockMethod(String className, String methodName, String signature) {
        String methodKey = className + "." + methodName + signature;
        return mockMethods.stream().anyMatch(existing -> {
            String existingKey = existing.getClassName() + "." + existing.getMethodName() + existing.getSignature();
            return existingKey.equals(methodKey);
        });
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
        private boolean throwException = false;
        private String exceptionType = "java.lang.RuntimeException";
        private String exceptionMessage = "Mocked exception";

        public MockRule() {}

        public MockRule(String returnValue, String returnType) {
            this.returnValue = returnValue;
            this.returnType = returnType;
        }

        public MockRule(String returnValue, String returnType, boolean throwException, String exceptionType, String exceptionMessage) {
            this.returnValue = returnValue;
            this.returnType = returnType;
            this.throwException = throwException;
            this.exceptionType = exceptionType;
            this.exceptionMessage = exceptionMessage;
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
        
        public boolean isThrowException() {
            return throwException;
        }
        
        public void setThrowException(boolean throwException) {
            this.throwException = throwException;
        }
        
        public String getExceptionType() {
            return exceptionType;
        }
        
        public void setExceptionType(String exceptionType) {
            this.exceptionType = exceptionType;
        }
        
        public String getExceptionMessage() {
            return exceptionMessage;
        }
        
        public void setExceptionMessage(String exceptionMessage) {
            this.exceptionMessage = exceptionMessage;
        }
    }
}
