package com.example.plugin.agent;

import com.example.plugin.mock.MockConfig;
import net.bytebuddy.asm.Advice;

import java.lang.reflect.Method;

/**
 * ByteBuddy Advice 拦截器
 */
public class MockInterceptor {
    
    @Advice.OnMethodExit
    public static void intercept(
        @Advice.Origin Method method,
        @Advice.Return(readOnly = false) Object returned
    ) {
        MockConfig config = MockAgent.getMockConfig();
        if (config == null) {
            return;
        }
        
        String className = method.getDeclaringClass().getName();
        String methodName = method.getName();
        
        MockConfig.MockRule rule = config.getMockRule(className, methodName);
        if (rule != null && rule.isEnabled()) {
            Object mockValue = parseMockValue(rule.getReturnValue(), rule.getReturnType());
            if (mockValue != null) {
                System.out.println("[MockAgent] Mocking " + className + "." + methodName + " -> " + mockValue);
                returned = mockValue;
            }
        }
    }
    
    private static Object parseMockValue(String value, String type) {
        if (value == null) {
            return null;
        }
        
        try {
            switch (type) {
                case "int":
                case "java.lang.Integer":
                    return Integer.parseInt(value);
                case "long":
                case "java.lang.Long":
                    return Long.parseLong(value);
                case "double":
                case "java.lang.Double":
                    return Double.parseDouble(value);
                case "float":
                case "java.lang.Float":
                    return Float.parseFloat(value);
                case "boolean":
                case "java.lang.Boolean":
                    return Boolean.parseBoolean(value);
                case "java.lang.String":
                default:
                    return value;
            }
        } catch (Exception e) {
            System.err.println("[MockAgent] Failed to parse mock value: " + e.getMessage());
            return null;
        }
    }
}
