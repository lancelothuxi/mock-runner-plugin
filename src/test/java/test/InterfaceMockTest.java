package test;

import java.lang.reflect.Proxy;
import java.util.List;

/**
 * Test class to demonstrate interface mocking with Dubbo/Feign style interfaces
 * 
 * Usage:
 * 1. Right-click on UserServiceApi.getUserById() and select "Add Mock for Method"
 * 2. Configure mock return value: {"userId":"123","username":"testuser","email":"test@example.com","age":25}
 * 3. Run this class with standard Run/Debug
 * 4. The mock agent will intercept the interface method call and return the mocked value
 */
public class InterfaceMockTest {
    
    public static void main(String[] args) {
        System.out.println("=== Testing Interface Mocking (Dubbo/Feign Style) ===\n");
        
        // Create a dynamic proxy for the interface (simulating Dubbo/Feign behavior)
        UserServiceApi userService = createProxy();
        
        // Test 1: Get user by ID
        System.out.println("Test 1: getUserById('123')");
        try {
            User user = userService.getUserById("123");
            System.out.println("Result: " + user);
            System.out.println("✅ Success!\n");
        } catch (Exception e) {
            System.out.println("❌ Error: " + e.getMessage() + "\n");
        }
        
        // Test 2: Get all users
        System.out.println("Test 2: getAllUsers()");
        try {
            List<User> users = userService.getAllUsers();
            System.out.println("Result: " + users);
            System.out.println("✅ Success!\n");
        } catch (Exception e) {
            System.out.println("❌ Error: " + e.getMessage() + "\n");
        }
        
        // Test 3: Create user
        System.out.println("Test 3: createUser(new User(...))");
        try {
            User newUser = new User("456", "newuser", "new@example.com", 30);
            boolean result = userService.createUser(newUser);
            System.out.println("Result: " + result);
            System.out.println("✅ Success!\n");
        } catch (Exception e) {
            System.out.println("❌ Error: " + e.getMessage() + "\n");
        }
        
        System.out.println("=== Test Complete ===");
        System.out.println("\nNote: If you see 'UnsupportedOperationException', it means the mock is not configured.");
        System.out.println("Configure mocks in the Mock Runner tool window and try again!");
    }
    
    /**
     * Create a dynamic proxy for the interface (simulating Dubbo/Feign)
     */
    private static UserServiceApi createProxy() {
        return (UserServiceApi) Proxy.newProxyInstance(
            UserServiceApi.class.getClassLoader(),
            new Class<?>[] { UserServiceApi.class },
            (proxy, method, args) -> {
                // This is where Dubbo/Feign would make the actual RPC call
                // But our Mock Agent will intercept before this point!
                throw new UnsupportedOperationException(
                    "No implementation for " + method.getName() + " - should be mocked!"
                );
            }
        );
    }
}
