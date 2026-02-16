package test.feign;

import org.testng.Assert;
import org.testng.annotations.*;
import java.lang.reflect.Proxy;
import java.util.List;

/**
 * Feign Client Mock Test with TestNG
 * 
 * HOW TO USE:
 * 1. Open Mock Runner tool window
 * 2. Add mock configurations for the methods you want to test
 * 3. Run this test with standard Run/Debug (TestNG will be used automatically)
 * 
 * MOCK CONFIGURATIONS:
 * 
 * For getUser:
 * - Class: test.feign.FeignUserClient
 * - Method: getUser
 * - Return Type: test.feign.UserResponse
 * - Return Value:
 * {
 *   "id": 1,
 *   "username": "john_doe",
 *   "email": "john@example.com",
 *   "phone": "+1234567890",
 *   "role": "ADMIN",
 *   "status": "ACTIVE"
 * }
 * 
 * For getAllUsers:
 * - Class: test.feign.FeignUserClient
 * - Method: getAllUsers
 * - Return Type: java.util.List<test.feign.UserResponse>
 * - Return Value:
 * [
 *   {
 *     "id": 1,
 *     "username": "john_doe",
 *     "email": "john@example.com",
 *     "phone": "+1234567890",
 *     "role": "ADMIN",
 *     "status": "ACTIVE"
 *   },
 *   {
 *     "id": 2,
 *     "username": "jane_smith",
 *     "email": "jane@example.com",
 *     "phone": "+0987654321",
 *     "role": "USER",
 *     "status": "ACTIVE"
 *   }
 * ]
 * 
 * For createUser:
 * - Class: test.feign.FeignUserClient
 * - Method: createUser
 * - Return Type: test.feign.UserResponse
 * - Return Value:
 * {
 *   "id": 999,
 *   "username": "new_user",
 *   "email": "newuser@example.com",
 *   "phone": "+1111111111",
 *   "role": "USER",
 *   "status": "ACTIVE"
 * }
 * 
 * For updateUser:
 * - Class: test.feign.FeignUserClient
 * - Method: updateUser
 * - Return Type: test.feign.UserResponse
 * - Return Value:
 * {
 *   "id": 1,
 *   "username": "john_doe",
 *   "email": "john.updated@example.com",
 *   "phone": "+9999999999",
 *   "role": "SUPER_ADMIN",
 *   "status": "ACTIVE"
 * }
 * 
 * For deleteUser:
 * - Class: test.feign.FeignUserClient
 * - Method: deleteUser
 * - Return Type: void
 * - Return Value: (leave empty for void)
 * 
 * For searchUsers:
 * - Class: test.feign.FeignUserClient
 * - Method: searchUsers
 * - Return Type: java.util.List<test.feign.UserResponse>
 * - Return Value:
 * [
 *   {
 *     "id": 1,
 *     "username": "john_doe",
 *     "email": "john@example.com",
 *     "phone": "+1234567890",
 *     "role": "ADMIN",
 *     "status": "ACTIVE"
 *   }
 * ]
 * 
 * EXCEPTION TEST:
 * For getUser (simulate 404 Not Found):
 * - Mode: Exception
 * - Exception Type: java.lang.RuntimeException
 * - Message: 404 User not found
 */
public class FeignClientTest {
    
    private FeignUserClient userClient;
    
    @BeforeClass
    public void setup() {
        System.out.println("╔════════════════════════════════════════════════════════════╗");
        System.out.println("║         Feign Client Interface Mock Test (TestNG)         ║");
        System.out.println("╚════════════════════════════════════════════════════════════╝");
        System.out.println();
        
        // Create Feign client proxy (simulating Spring Cloud Feign behavior)
        userClient = createFeignProxy();
    }
    
    @AfterClass
    public void teardown() {
        System.out.println();
        System.out.println("╔════════════════════════════════════════════════════════════╗");
        System.out.println("║                    Test Complete                          ║");
        System.out.println("╚════════════════════════════════════════════════════════════╝");
    }
    
    @Test(priority = 1, description = "Test GET /api/users/{id}")
    public void testGetUser() {
        System.out.println("━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━");
        System.out.println("Test 1: GET /api/users/1 - getUser(1L)");
        System.out.println("━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━");
        
        UserResponse user = userClient.getUser(1L);
        
        Assert.assertNotNull(user, "User should not be null");
        Assert.assertEquals(user.getId(), Long.valueOf(1L), "User ID should match");
        Assert.assertEquals(user.getUsername(), "john_doe", "Username should match");
        Assert.assertEquals(user.getRole(), "ADMIN", "Role should be ADMIN");
        Assert.assertEquals(user.getStatus(), "ACTIVE", "Status should be ACTIVE");
        
        System.out.println("✅ SUCCESS");
        System.out.println("   User ID: " + user.getId());
        System.out.println("   Username: " + user.getUsername());
        System.out.println("   Email: " + user.getEmail());
        System.out.println("   Role: " + user.getRole());
        System.out.println();
    }
    
    @Test(priority = 2, description = "Test GET /api/users")
    public void testGetAllUsers() {
        System.out.println("━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━");
        System.out.println("Test 2: GET /api/users - getAllUsers()");
        System.out.println("━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━");
        
        List<UserResponse> users = userClient.getAllUsers();
        
        Assert.assertNotNull(users, "Users list should not be null");
        Assert.assertTrue(users.size() >= 2, "Should have at least 2 users");
        Assert.assertEquals(users.get(0).getUsername(), "john_doe", "First user should be john_doe");
        Assert.assertEquals(users.get(1).getUsername(), "jane_smith", "Second user should be jane_smith");
        
        System.out.println("✅ SUCCESS");
        System.out.println("   Found " + users.size() + " users:");
        for (UserResponse user : users) {
            System.out.println("   - " + user.getUsername() + " (" + user.getEmail() + ") - " + user.getRole());
        }
        System.out.println();
    }
    
    @Test(priority = 3, description = "Test POST /api/users")
    public void testCreateUser() {
        System.out.println("━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━");
        System.out.println("Test 3: POST /api/users - createUser(request)");
        System.out.println("━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━");
        
        CreateUserRequest request = new CreateUserRequest(
            "new_user",
            "newuser@example.com",
            "password123",
            "+1111111111",
            "USER"
        );
        
        UserResponse newUser = userClient.createUser(request);
        
        Assert.assertNotNull(newUser, "Created user should not be null");
        Assert.assertNotNull(newUser.getId(), "User ID should be assigned");
        Assert.assertEquals(newUser.getUsername(), "new_user", "Username should match");
        Assert.assertEquals(newUser.getStatus(), "ACTIVE", "New user status should be ACTIVE");
        
        System.out.println("✅ SUCCESS");
        System.out.println("   Created User ID: " + newUser.getId());
        System.out.println("   Username: " + newUser.getUsername());
        System.out.println("   Email: " + newUser.getEmail());
        System.out.println();
    }
    
    @Test(priority = 4, description = "Test PUT /api/users/{id}")
    public void testUpdateUser() {
        System.out.println("━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━");
        System.out.println("Test 4: PUT /api/users/1 - updateUser(1L, request)");
        System.out.println("━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━");
        
        UpdateUserRequest request = new UpdateUserRequest(
            "john.updated@example.com",
            "+9999999999",
            "SUPER_ADMIN",
            "ACTIVE"
        );
        
        UserResponse updatedUser = userClient.updateUser(1L, request);
        
        Assert.assertNotNull(updatedUser, "Updated user should not be null");
        Assert.assertEquals(updatedUser.getId(), Long.valueOf(1L), "User ID should match");
        Assert.assertEquals(updatedUser.getEmail(), "john.updated@example.com", "Email should be updated");
        Assert.assertEquals(updatedUser.getRole(), "SUPER_ADMIN", "Role should be updated");
        
        System.out.println("✅ SUCCESS");
        System.out.println("   Updated User ID: " + updatedUser.getId());
        System.out.println("   New Email: " + updatedUser.getEmail());
        System.out.println("   New Role: " + updatedUser.getRole());
        System.out.println();
    }
    
    @Test(priority = 5, description = "Test DELETE /api/users/{id}")
    public void testDeleteUser() {
        System.out.println("━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━");
        System.out.println("Test 5: DELETE /api/users/1 - deleteUser(1L)");
        System.out.println("━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━");
        
        // Should not throw exception
        userClient.deleteUser(1L);
        
        System.out.println("✅ SUCCESS");
        System.out.println("   User deleted successfully");
        System.out.println();
    }
    
    @Test(priority = 6, description = "Test GET /api/users/search")
    public void testSearchUsers() {
        System.out.println("━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━");
        System.out.println("Test 6: GET /api/users/search?keyword=john - searchUsers('john')");
        System.out.println("━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━");
        
        List<UserResponse> results = userClient.searchUsers("john");
        
        Assert.assertNotNull(results, "Search results should not be null");
        Assert.assertTrue(results.size() > 0, "Should find at least 1 user");
        Assert.assertTrue(
            results.get(0).getUsername().contains("john"),
            "Result should contain 'john'"
        );
        
        System.out.println("✅ SUCCESS");
        System.out.println("   Found " + results.size() + " user(s):");
        for (UserResponse user : results) {
            System.out.println("   - " + user.getUsername() + " (" + user.getEmail() + ")");
        }
        System.out.println();
    }
    
    @Test(priority = 7, description = "Test 404 exception handling", enabled = false)
    public void testUserNotFound() {
        System.out.println("━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━");
        System.out.println("Test 7: Exception Test - getUser with 404 exception mock");
        System.out.println("━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━");
        System.out.println("⚠️  Enable this test and configure exception mock:");
        System.out.println("   - Mode: Exception");
        System.out.println("   - Exception Type: java.lang.RuntimeException");
        System.out.println("   - Message: 404 User not found");
        System.out.println();
        
        Assert.assertThrows(RuntimeException.class, () -> {
            userClient.getUser(999L);
        });
        
        System.out.println("✅ SUCCESS - Exception thrown as expected");
        System.out.println();
    }
    
    /**
     * Create Feign client proxy (simulating Spring Cloud Feign)
     * In real Spring Cloud, this would be done by @FeignClient annotation
     */
    private FeignUserClient createFeignProxy() {
        return (FeignUserClient) Proxy.newProxyInstance(
            FeignUserClient.class.getClassLoader(),
            new Class<?>[] { FeignUserClient.class },
            (proxy, method, args) -> {
                // This simulates Feign's HTTP call
                // In reality, Feign would make an actual HTTP request
                // Our Mock Agent intercepts BEFORE this point!
                throw new UnsupportedOperationException(
                    "Feign HTTP call not mocked: " + method.getName() + 
                    " - Configure mock in Mock Runner tool window!"
                );
            }
        );
    }
}
