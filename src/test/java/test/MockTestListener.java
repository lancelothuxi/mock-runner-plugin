package test;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import org.testng.IExecutionListener;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.lang.management.ManagementFactory;
import java.util.HashMap;
import java.util.Map;

/**
 * TestNG Listener that automatically generates mock configuration
 * and verifies agent is attached
 */
public class MockTestListener implements IExecutionListener {
    
    private static final String AGENT_JAR = "build/libs/mock-agent-1.0.6-agent.jar";
    private static final String MOCK_CONFIG = "src/test/resources/mock-config-test.json";
    
    @Override
    public void onExecutionStart() {
        System.out.println("╔════════════════════════════════════════════════════════════╗");
        System.out.println("║           Mock Runner Test Execution Starting             ║");
        System.out.println("╚════════════════════════════════════════════════════════════╝");
        System.out.println();
        
        // Check if agent is attached
        checkAgentAttached();
        
        // Verify mock configuration exists
        checkMockConfiguration();
        
        System.out.println();
    }
    
    @Override
    public void onExecutionFinish() {
        System.out.println();
        System.out.println("╔════════════════════════════════════════════════════════════╗");
        System.out.println("║           Mock Runner Test Execution Finished             ║");
        System.out.println("╚════════════════════════════════════════════════════════════╝");
    }
    
    private void checkAgentAttached() {
        String jvmArgs = ManagementFactory.getRuntimeMXBean().getInputArguments().toString();
        
        if (jvmArgs.contains("-javaagent") && jvmArgs.contains("mock-agent")) {
            System.out.println("✓ Mock Agent is attached");
            System.out.println("  JVM Args: " + jvmArgs);
        } else {
            System.out.println("⚠ WARNING: Mock Agent is NOT attached!");
            System.out.println("  Current JVM Args: " + jvmArgs);
            System.out.println();
            System.out.println("  To attach agent, run tests with:");
            System.out.println("    ./gradlew-java17.sh test");
            System.out.println();
            System.out.println("  Or add VM option in IDE:");
            System.out.println("    -javaagent:" + new File(AGENT_JAR).getAbsolutePath() + "=" + new File(MOCK_CONFIG).getAbsolutePath());
            System.out.println();
            System.out.println("  Tests will fail with UnsupportedOperationException!");
        }
    }
    
    private void checkMockConfiguration() {
        File configFile = new File(MOCK_CONFIG);
        
        if (configFile.exists()) {
            System.out.println("✓ Mock configuration found: " + configFile.getAbsolutePath());
            System.out.println("  File size: " + configFile.length() + " bytes");
        } else {
            System.out.println("⚠ WARNING: Mock configuration not found!");
            System.out.println("  Expected: " + configFile.getAbsolutePath());
            System.out.println();
            System.out.println("  Creating default configuration...");
            createDefaultMockConfiguration(configFile);
        }
    }
    
    private void createDefaultMockConfiguration(File configFile) {
        try {
            configFile.getParentFile().mkdirs();
            
            Map<String, Object> config = new HashMap<>();
            Map<String, MockRule> mockRules = new HashMap<>();
            
            // Add Dubbo mocks
            mockRules.put("test.dubbo.DubboOrderService.getOrderById", 
                new MockRule("{\"orderId\":12345,\"userId\":100,\"orderNo\":\"ORD-2026-001\",\"totalAmount\":299.99,\"status\":\"COMPLETED\"}", 
                            "test.dubbo.OrderDTO", true, false));
            
            mockRules.put("test.dubbo.DubboOrderService.getOrdersByUserId",
                new MockRule("[{\"orderId\":12345,\"userId\":100,\"orderNo\":\"ORD-2026-001\",\"totalAmount\":299.99,\"status\":\"COMPLETED\"},{\"orderId\":12346,\"userId\":100,\"orderNo\":\"ORD-2026-002\",\"totalAmount\":599.99,\"status\":\"SHIPPED\"}]",
                            "java.util.List<test.dubbo.OrderDTO>", true, false));
            
            mockRules.put("test.dubbo.DubboOrderService.createOrder",
                new MockRule("{\"orderId\":99999,\"userId\":100,\"orderNo\":\"ORD-2026-NEW\",\"totalAmount\":199.99,\"status\":\"PENDING\"}",
                            "test.dubbo.OrderDTO", true, false));
            
            mockRules.put("test.dubbo.DubboOrderService.cancelOrder",
                new MockRule("true", "boolean", true, false));
            
            mockRules.put("test.dubbo.DubboOrderService.getOrderCount",
                new MockRule("5", "int", true, false));
            
            // Add Feign mocks
            mockRules.put("test.feign.FeignUserClient.getUser",
                new MockRule("{\"id\":1,\"username\":\"john_doe\",\"email\":\"john@example.com\",\"phone\":\"+1234567890\",\"role\":\"ADMIN\",\"status\":\"ACTIVE\"}",
                            "test.feign.UserResponse", true, false));
            
            mockRules.put("test.feign.FeignUserClient.getAllUsers",
                new MockRule("[{\"id\":1,\"username\":\"john_doe\",\"email\":\"john@example.com\",\"phone\":\"+1234567890\",\"role\":\"ADMIN\",\"status\":\"ACTIVE\"},{\"id\":2,\"username\":\"jane_smith\",\"email\":\"jane@example.com\",\"phone\":\"+0987654321\",\"role\":\"USER\",\"status\":\"ACTIVE\"}]",
                            "java.util.List<test.feign.UserResponse>", true, false));
            
            mockRules.put("test.feign.FeignUserClient.createUser",
                new MockRule("{\"id\":999,\"username\":\"new_user\",\"email\":\"newuser@example.com\",\"phone\":\"+1111111111\",\"role\":\"USER\",\"status\":\"ACTIVE\"}",
                            "test.feign.UserResponse", true, false));
            
            mockRules.put("test.feign.FeignUserClient.updateUser",
                new MockRule("{\"id\":1,\"username\":\"john_doe\",\"email\":\"john.updated@example.com\",\"phone\":\"+9999999999\",\"role\":\"SUPER_ADMIN\",\"status\":\"ACTIVE\"}",
                            "test.feign.UserResponse", true, false));
            
            mockRules.put("test.feign.FeignUserClient.deleteUser",
                new MockRule("", "void", true, false));
            
            mockRules.put("test.feign.FeignUserClient.searchUsers",
                new MockRule("[{\"id\":1,\"username\":\"john_doe\",\"email\":\"john@example.com\",\"phone\":\"+1234567890\",\"role\":\"ADMIN\",\"status\":\"ACTIVE\"}]",
                            "java.util.List<test.feign.UserResponse>", true, false));
            
            config.put("mockRules", mockRules);
            config.put("mockMethods", new Object[0]);
            
            Gson gson = new GsonBuilder().setPrettyPrinting().create();
            try (FileWriter writer = new FileWriter(configFile)) {
                gson.toJson(config, writer);
            }
            
            System.out.println("✓ Created default mock configuration: " + configFile.getAbsolutePath());
            
        } catch (IOException e) {
            System.err.println("✗ Failed to create mock configuration: " + e.getMessage());
            e.printStackTrace();
        }
    }
    
    private static class MockRule {
        private final String returnValue;
        private final String returnType;
        private final boolean enabled;
        private final boolean throwException;
        
        public MockRule(String returnValue, String returnType, boolean enabled, boolean throwException) {
            this.returnValue = returnValue;
            this.returnType = returnType;
            this.enabled = enabled;
            this.throwException = throwException;
        }
    }
}
