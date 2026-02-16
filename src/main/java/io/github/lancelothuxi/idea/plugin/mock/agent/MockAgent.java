package io.github.lancelothuxi.idea.plugin.mock.agent;

import io.github.lancelothuxi.idea.plugin.mock.mock.MockConfig;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import net.bytebuddy.agent.builder.AgentBuilder;
import net.bytebuddy.implementation.MethodDelegation;
import net.bytebuddy.implementation.bind.annotation.AllArguments;
import net.bytebuddy.implementation.bind.annotation.Origin;
import net.bytebuddy.implementation.bind.annotation.RuntimeType;
import net.bytebuddy.implementation.bind.annotation.SuperCall;
import net.bytebuddy.matcher.ElementMatchers;
import net.bytebuddy.description.type.TypeDescription;

import java.io.FileReader;
import java.lang.instrument.Instrumentation;
import java.lang.reflect.Method;
import java.lang.reflect.Type;
import java.util.Map;
import java.util.logging.Level;
import java.util.logging.Logger;

public class MockAgent {
    
    public static final Logger LOG = Logger.getLogger(MockAgent.class.getName());
    public static MockConfig mockConfig;
    
    public static void premain(String agentArgs, Instrumentation inst) {
        LOG.info("========================================");
        LOG.info("[MockAgent] Starting Mock Agent...");
        LOG.info("[MockAgent] Agent args: " + agentArgs);
        LOG.info("========================================");
        
        if (agentArgs != null && !agentArgs.isEmpty()) {
            loadMockConfig(agentArgs);
        } else {
            LOG.severe("[MockAgent] No config file path provided!");
        }
        
        if (mockConfig == null) {
            LOG.severe("[MockAgent] mockConfig is null after loading!");
            return;
        }
        
        if (mockConfig.getAllRules().isEmpty()) {
            LOG.warning("[MockAgent] No mock rules configured");
            LOG.info("[MockAgent] mockRules size: " + mockConfig.getAllRules().size());
            return;
        }
        
        LOG.info("[MockAgent] Loaded " + mockConfig.getAllRules().size() + " mock rules");
        for (Map.Entry<String, MockConfig.MockRule> entry : mockConfig.getAllRules().entrySet()) {
            MockConfig.MockRule rule = entry.getValue();
            LOG.info("[MockAgent]   - " + entry.getKey() + " -> " + rule.getReturnValue() + " (type: " + rule.getReturnType() + ")");
        }
        
        new AgentBuilder.Default()
            .type(ElementMatchers.any())
            .transform((builder, typeDescription, classLoader, module, protectionDomain) -> {
                String className = typeDescription.getName();
                boolean isInterface = typeDescription.isInterface();
                
                for (Map.Entry<String, MockConfig.MockRule> entry : mockConfig.getAllRules().entrySet()) {
                    String key = entry.getKey();
                    if (key.startsWith(className + ".")) {
                        String methodName = key.substring(className.length() + 1);
                        MockConfig.MockRule rule = entry.getValue();
                        
                        if (rule.isEnabled()) {
                            LOG.info("[MockAgent] *** Intercepting " + className + "." + methodName + " (interface: " + isInterface + ") ***");
                            
                            if (isInterface) {
                                // For interfaces (Dubbo/Feign), use InterfaceInterceptor without SuperCall
                                builder = builder.method(ElementMatchers.named(methodName))
                                    .intercept(MethodDelegation.to(InterfaceInterceptor.class));
                            } else {
                                // For concrete classes, use regular Interceptor with SuperCall
                                builder = builder.method(ElementMatchers.named(methodName))
                                    .intercept(MethodDelegation.to(Interceptor.class));
                            }
                        }
                    }
                }
                
                return builder;
            })
            .installOn(inst);
        
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
    
    public static MockConfig getMockConfig() {
        return mockConfig;
    }
    
    public static class Interceptor {

        @RuntimeType
        public static Object intercept(@Origin Method method,
                                        @AllArguments Object[] args,
                                        @SuperCall java.util.concurrent.Callable<?> zuper) throws Exception {
            try {
                LOG.info("[MockAgent] *** Method called: " + method.getDeclaringClass().getName() + "." + method.getName() + " ***");

                MockConfig config = MockAgent.mockConfig;
                if (config == null) {
                    LOG.warning("[MockAgent] Config is null!");
                    return zuper.call();
                }

                String className = method.getDeclaringClass().getName();
                String methodName = method.getName();

                LOG.info("[MockAgent] Looking for mock rule: " + className + "." + methodName);
                MockConfig.MockRule rule = config.getMockRule(className, methodName);

                if (rule != null && rule.isEnabled()) {
                    LOG.info("[MockAgent] Found rule - returnValue: " + rule.getReturnValue() + ", returnType: " + rule.getReturnType());
                    
                    // Check if this is exception mode
                    if (rule.isThrowException()) {
                        LOG.info("[MockAgent] *** THROWING EXCEPTION: " + rule.getExceptionType() + " ***");
                        throw createException(rule.getExceptionType(), rule.getExceptionMessage());
                    }
                    
                    Object mockValue = parseMockValue(rule.getReturnValue(), rule.getReturnType());
                    LOG.info("[MockAgent] *** RETURNING MOCK VALUE: " + mockValue + " (class: " + (mockValue != null ? mockValue.getClass().getName() : "null") + ") ***");
                    return mockValue;
                } else {
                    LOG.info("[MockAgent] No mock rule found or rule disabled, calling original method");
                    return zuper.call();
                }

            } catch (Exception e) {
                LOG.log(Level.SEVERE, "[MockAgent] Exception in interceptor", e);
                throw e;
            }
        }

        public static Object parseMockValue(String value, String type) {
            LOG.info("[MockAgent] *** parseMockValue called with value: " + value + ", type: " + type + " ***");
                
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
                        return value;
                }

                if (value.startsWith("[") || value.startsWith("{")) {
                    Gson gson = new Gson();

                    // Handle generic types like List<Student>
                    if (type.contains("<") && type.contains(">")) {
                        LOG.info("[MockAgent] Processing generic type: " + type);
                        
                        // Handle List<ClassName>
                        if ((type.startsWith("List<") || type.startsWith("java.util.List<")) && type.endsWith(">")) {
                            int startIdx = type.indexOf('<'); String innerType = type.substring(startIdx + 1, type.length() - 1);
                            LOG.info("[MockAgent] Extracted inner type: " + innerType);
                            try {
                                Class<?> innerClass = Class.forName(innerType);
                                Type listType = TypeToken.getParameterized(java.util.List.class, innerClass).getType();
                                Object result = gson.fromJson(value, listType);
                                LOG.info("[MockAgent] Successfully parsed List with proper types: " + result);
                                return result;
                            } catch (ClassNotFoundException e) {
                                LOG.warning("[MockAgent] Class not found: " + innerType + ", error: " + e.getMessage());
                                return gson.fromJson(value, java.util.List.class);
                            }
                        }
                    }

                    // Handle non-generic types
                    if (type.contains("List") || type.equals("java.util.List")) {
                        return gson.fromJson(value, java.util.List.class);
                    } else if (type.contains("Map") || type.equals("java.util.Map")) {
                        return gson.fromJson(value, java.util.Map.class);
                    } else {
                        try {
                            Class<?> clazz = Class.forName(type);
                            Object result = gson.fromJson(value, clazz);
                            LOG.info("[MockAgent] Parsed with Class.forName: " + result);
                            return result;
                        } catch (ClassNotFoundException e) {
                            LOG.warning("[MockAgent] Class not found: " + type + ", falling back to generic parsing");
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
        
        private static Exception createException(String exceptionType, String message) throws Exception {
            try {
                Class<?> exceptionClass = Class.forName(exceptionType);
                if (Exception.class.isAssignableFrom(exceptionClass)) {
                    return (Exception) exceptionClass.getConstructor(String.class).newInstance(message);
                }
            } catch (Exception e) {
                LOG.warning("[MockAgent] Failed to create exception: " + exceptionType + ", using RuntimeException");
            }
            return new RuntimeException(message);
        }
    }
    
    /**
     * Interceptor for interface methods (Dubbo/Feign) - no SuperCall needed
     */
    public static class InterfaceInterceptor {

        @RuntimeType
        public static Object intercept(@Origin Method method,
                                        @AllArguments Object[] args) throws Exception {
            try {
                LOG.info("[MockAgent] *** Interface method called: " + method.getDeclaringClass().getName() + "." + method.getName() + " ***");

                MockConfig config = MockAgent.mockConfig;
                if (config == null) {
                    LOG.severe("[MockAgent] Config is null for interface method!");
                    throw new IllegalStateException("MockAgent config is null");
                }

                String className = method.getDeclaringClass().getName();
                String methodName = method.getName();

                LOG.info("[MockAgent] Looking for mock rule: " + className + "." + methodName);
                MockConfig.MockRule rule = config.getMockRule(className, methodName);

                if (rule != null && rule.isEnabled()) {
                    LOG.info("[MockAgent] Found rule - returnValue: " + rule.getReturnValue() + ", returnType: " + rule.getReturnType());
                    
                    // Check if this is exception mode
                    if (rule.isThrowException()) {
                        LOG.info("[MockAgent] *** THROWING EXCEPTION: " + rule.getExceptionType() + " ***");
                        throw createException(rule.getExceptionType(), rule.getExceptionMessage());
                    }
                    
                    Object mockValue = Interceptor.parseMockValue(rule.getReturnValue(), rule.getReturnType());
                    LOG.info("[MockAgent] *** RETURNING MOCK VALUE: " + mockValue + " (class: " + (mockValue != null ? mockValue.getClass().getName() : "null") + ") ***");
                    return mockValue;
                } else {
                    LOG.warning("[MockAgent] No mock rule found for interface method: " + className + "." + methodName);
                    throw new UnsupportedOperationException("No mock configured for interface method: " + className + "." + methodName);
                }

            } catch (Exception e) {
                LOG.log(Level.SEVERE, "[MockAgent] Exception in interface interceptor", e);
                throw e;
            }
        }
        
        private static Exception createException(String exceptionType, String message) throws Exception {
            try {
                Class<?> exceptionClass = Class.forName(exceptionType);
                if (Exception.class.isAssignableFrom(exceptionClass)) {
                    return (Exception) exceptionClass.getConstructor(String.class).newInstance(message);
                }
            } catch (Exception e) {
                LOG.warning("[MockAgent] Failed to create exception: " + exceptionType + ", using RuntimeException");
            }
            return new RuntimeException(message);
        }
    }
}
