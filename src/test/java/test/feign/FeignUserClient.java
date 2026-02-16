package test.feign;

import java.util.List;

/**
 * Simulated Feign Client Interface
 * In real Spring Cloud, this would be annotated with @FeignClient
 * 
 * Example:
 * @FeignClient(name = "user-service", url = "http://localhost:8080")
 */
public interface FeignUserClient {
    
    /**
     * GET /api/users/{id}
     */
    UserResponse getUser(Long id);
    
    /**
     * GET /api/users
     */
    List<UserResponse> getAllUsers();
    
    /**
     * POST /api/users
     */
    UserResponse createUser(CreateUserRequest request);
    
    /**
     * PUT /api/users/{id}
     */
    UserResponse updateUser(Long id, UpdateUserRequest request);
    
    /**
     * DELETE /api/users/{id}
     */
    void deleteUser(Long id);
    
    /**
     * GET /api/users/search?keyword={keyword}
     */
    List<UserResponse> searchUsers(String keyword);
}
