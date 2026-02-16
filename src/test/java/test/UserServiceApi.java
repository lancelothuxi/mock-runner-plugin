package test;

import java.util.List;

/**
 * Example Dubbo/Feign interface for testing
 * This interface has no implementation - perfect for testing interface mocking
 */
public interface UserServiceApi {
    
    /**
     * Get user by ID
     */
    User getUserById(String userId);
    
    /**
     * Get all users
     */
    List<User> getAllUsers();
    
    /**
     * Create a new user
     */
    boolean createUser(User user);
}
