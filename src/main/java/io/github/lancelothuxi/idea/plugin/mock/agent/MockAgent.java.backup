package com.example.plugin.agent;

import com.example.plugin.mock.MockConfig;
import com.google.gson.Gson;
import net.bytebuddy.agent.builder.AgentBuilder;
import net.bytebuddy.implementation.MethodDelegation;
import net.bytebuddy.implementation.bind.annotation.AllArguments;
import net.bytebuddy.implementation.bind.annotation.Origin;
import net.bytebuddy.implementation.bind.annotation.RuntimeType;
import net.bytebuddy.implementation.bind.annotation.SuperCall;
import net.bytebuddy.matcher.ElementMatchers;

import java.io.FileReader;
import java.lang.instrument.Instrumentation;
import java.lang.reflect.Method;
import java.util.Map;
import java.util.concurrent.Callable;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 * Mock JavaAgent - 使用 ByteBuddy MethodDelegation 拦截方法调用
 */
public class MockAgent {
    
    public static final Logger LOG = Logger.getLogger(MockAgent.class.getName());
    public static MockConfig mockConfig;
    
    public static void premain(String agentArgs, Instrumentation inst) {
        System.out.println("========================================");
        System.out.println("[MockAgent] Starting Mock Agent...");
        System.out.println("[MockAgent] Agent args: " + agentArgs);
        System.out.println("========================================");
        
        LOG.info("========================================");
        LOG.info("[MockAgent] Starting Mock Agent...");
        LOG.info("[MockAgent] Agent args: " + agentArgs);
        LOG.info("========================================");
        
        // 加载 Mock 配置
        if (agentArgs != null && !agentArgs.isEmpty()) {
            System.out.println("[MockAgent] Loading config from: " + agentArgs);
            loadMockConfig(agentArgs);
        } else {
            System.err.println("[MockAgent] No config file path provided!");
            LOG.severe("[MockAgent] No config file path provided!");
        }
        
        if (mockConfig == null) {
            System.err.println("[MockAgent] mockConfig is null after loading!");
            LOG.severe("[MockAgent] mockConfig is null after loading!");
            return;
        }
        
        System.out.println("[MockAgent] Config loaded, checking rules...");
        
        if (mockConfig.getAllRules().isEmpty()) {
            System.out.println("[MockAgent] No mock rules configured");
            LOG.warning("[MockAgent] No mock rules configured");
            return;
        }
        
        System.out.println("[MockAgent] Loaded " + mockConfig.getAllRules().size() + " mock rules");
        LOG.info("[MockAgent] Loaded " + mockConfig.getAllRules().size() + " mock rules");
        for (Map.Entry<String, MockConfig.MockRule> entry : mockConfig.getAllRules().entrySet()) {
            System.out.println("[MockAgent]   - " + entry.getKey() + " -> " + entry.getValue().getReturnValue());
            LOG.info("[MockAgent]   - " + entry.getKey() + " -> " + entry.getValue().getReturnValue());
        }
        
        // 使用 ByteBuddy 构建 Agent
        new AgentBuilder.Default()
            .type(ElementMatchers.any())
            .transform((builder, typeDescription, classLoader, module, protectionDomain) -> {
                String className = typeDescription.getName();
                
                // 检查是否有该类的 Mock 规则
                for (Map.Entry<String, MockConfig.MockRule> entry : mockConfig.getAllRules().entrySet()) {
                    String key = entry.getKey();
                    if (key.startsWith(className + ".")) {
                        String methodName = key.substring(className.length() + 1);
                        MockConfig.MockRule rule = entry.getValue();
                        
                        if (rule.isEnabled()) {
                            System.out.println("[MockAgent] *** Intercepting " + className + "." + methodName + " ***");
                            LOG.info("[MockAgent] *** Intercepting " + className + "." + methodName + " ***");
                            
                            // 使用 MethodDelegation 拦截方法
                            builder = builder.method(ElementMatchers.named(methodName))
                                .intercept(MethodDelegation.to(Interceptor.class));
                        }
                    }
                }
                
                return builder;
            })
            .installOn(inst);
        
        System.out.println("========================================");
        System.out.println("[MockAgent] Mock Agent installed successfully");
        System.out.println("========================================");
        LOG.info("========================================");
        LOG.info("[MockAgent] Mock Agent installed successfully");
        LOG.info("========================================");
    }
    
    private static void loadMockConfig(String configPath) {
        try {
            LOG.info("[MockAgent] Loading config from: " + configPath);
            Gson gson = new Gson();
            mockConfig = gson.fromJson(new FileReader(configPath), MockConfig.class);
            
            if (mockConfig != null) {
                LOG.info("[MockAgent] Config loaded successfully");
                LOG.info("[MockAgent] mockRules size: " + mockConfig.getAllRules().size());
                
                // 如果 mockRules 为空但 mockMethods 不为空，重建 mockRules
                if (mockConfig.getAllRules().isEmpty() && !mockConfig.getMockMethods().isEmpty()) {
                    LOG.info("[MockAgent] mockRules is empty, rebuilding from mockMethods...");
                    mockConfig.rebuildMockRules();
                    LOG.info("[MockAgent] After rebuild, mockRules size: " + mockConfig.getAllRules().size());
                }
            } else {
                LOG.severe("[MockAgent] Config is null after loading!");
            }
        } catch (Exception e) {
            LOG.log(Level.SEVERE, "[MockAgent] Failed to load mock config", e);
        }
    }
    
    /**
     * ByteBuddy MethodDelegation 拦截器
     */
    public static class Interceptor {
        
        @RuntimeType
        public static Object intercept(@Origin Method method,
                                        @AllArguments Object[] args,
                                        @SuperCall Callable<?> zuper) throws Exception {
            try {
                System.out.println("[MockAgent] *** Method called: " + method.getDeclaringClass().getName() + "." + method.getName() + " ***");
                LOG.info("[MockAgent] *** Method called: " + method.getDeclaringClass().getName() + "." + method.getName() + " ***");
                
                MockConfig config = MockAgent.mockConfig;
                if (config == null) {
                    System.err.println("[MockAgent] Config is null!");
                    LOG.warning("[MockAgent] Config is null!");
                    return zuper.call();
                }
                
                String className = method.getDeclaringClass().getName();
                String methodName = method.getName();
                
                System.out.println("[MockAgent] Looking for mock rule: " + className + "." + methodName);
                LOG.info("[MockAgent] Looking for mock rule: " + className + "." + methodName);
                
                MockConfig.MockRule rule = config.getMockRule(className, methodName);
                
                if (rule != null && rule.isEnabled()) {
                    Object mockValue = parseMockValue(rule.getReturnValue(), rule.getReturnType());
                    System.out.println("[MockAgent] *** RETURNING MOCK VALUE: " + mockValue + " (class: " + (mockValue != null ? mockValue.getClass().getName() : "null") + ") ***");
                    LOG.info("[MockAgent] *** RETURNING MOCK VALUE: " + mockValue + " (class: " + (mockValue != null ? mockValue.getClass().getName() : "null") + ") ***");
                    return mockValue;
                } else {
                    System.out.println("[MockAgent] No mock rule found or rule disabled, calling original method");
                    LOG.info("[MockAgent] No mock rule found or rule disabled, calling original method");
                    return zuper.call();
                }
            } catch (Exception e) {
                System.err.println("[MockAgent] Exception in interceptor: " + e.getMessage());
                LOG.log(Level.SEVERE, "[MockAgent] Exception in interceptor", e);
                throw e;
            }
        }
        
        private static Object parseMockValue(String value, String type) {
            if (value == null) {
                return null;
            }
            
            try {
                // 处理基本类型
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
                        return value;
                }
                
                // 对于复杂类型，使用 Gson 解析
                if (value.startsWith("[") || value.startsWith("{")) {
                    Gson gson = new Gson();
                    
                    if (type.contains("List") || type.equals("java.util.List")) {
                        return gson.fromJson(value, java.util.List.class);
                    } else if (type.contains("Map") || type.equals("java.util.Map")) {
                        return gson.fromJson(value, java.util.Map.class);
                    } else {
                        try {
                            Class<?> clazz = Class.forName(type);
                            return gson.fromJson(value, clazz);
                        } catch (ClassNotFoundException e) {
                            if (value.startsWith("[")) {
                                return gson.fromJson(value, java.util.List.class);
                            } else {
                                return gson.fromJson(value, java.util.Map.class);
                            }
                        }
                    }
                }
                
                return value;
                
            } catch (Exception e) {
                LOG.log(Level.SEVERE, "[MockAgent] Failed to parse mock value", e);
                return null;
            }
        }
    }
}
