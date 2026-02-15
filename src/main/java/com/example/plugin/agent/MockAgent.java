package com.example.plugin.agent;

import com.example.plugin.mock.MockConfig;
import com.google.gson.Gson;
import net.bytebuddy.agent.builder.AgentBuilder;
import net.bytebuddy.asm.Advice;
import net.bytebuddy.matcher.ElementMatchers;

import java.io.FileReader;
import java.lang.instrument.Instrumentation;
import java.util.Map;

/**
 * Mock JavaAgent - 使用 ByteBuddy 拦截方法调用
 */
public class MockAgent {
    
    private static MockConfig mockConfig;
    
    public static void premain(String agentArgs, Instrumentation inst) {
        System.out.println("[MockAgent] Starting Mock Agent...");
        
        // 加载 Mock 配置
        if (agentArgs != null && !agentArgs.isEmpty()) {
            loadMockConfig(agentArgs);
        }
        
        if (mockConfig == null || mockConfig.getAllRules().isEmpty()) {
            System.out.println("[MockAgent] No mock rules configured");
            return;
        }
        
        System.out.println("[MockAgent] Loaded " + mockConfig.getAllRules().size() + " mock rules");
        
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
                            System.out.println("[MockAgent] Intercepting " + className + "." + methodName);
                            builder = builder.method(ElementMatchers.named(methodName))
                                .intercept(Advice.to(MockInterceptor.class));
                        }
                    }
                }
                
                return builder;
            })
            .installOn(inst);
        
        System.out.println("[MockAgent] Mock Agent installed successfully");
    }
    
    private static void loadMockConfig(String configPath) {
        try {
            Gson gson = new Gson();
            mockConfig = gson.fromJson(new FileReader(configPath), MockConfig.class);
            System.out.println("[MockAgent] Loaded mock config from: " + configPath);
        } catch (Exception e) {
            System.err.println("[MockAgent] Failed to load mock config: " + e.getMessage());
            e.printStackTrace();
        }
    }
    
    public static MockConfig getMockConfig() {
        return mockConfig;
    }
}
