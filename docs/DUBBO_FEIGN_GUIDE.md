# Dubbo & Feign Interface Mocking Guide

This guide explains how to use Mock Runner with Dubbo and Feign interface-based RPC frameworks.

## Overview

Dubbo and Feign use interface-based programming where:
- You define an interface with method signatures
- The framework generates dynamic proxies at runtime
- No concrete implementation class exists in your code

Mock Runner fully supports this pattern by detecting interface methods and intercepting them directly.

## How It Works

### Traditional Class Mocking
```java
public class UserService {
    public User getUser(String id) {
        // Real implementation
        return database.findUser(id);
    }
}
```
- Agent intercepts the method
- If no mock configured, calls original implementation via `@SuperCall`

### Interface Mocking (Dubbo/Feign)
```java
public interface UserServiceApi {
    User getUser(String id);
    // No implementation!
}
```
- Agent detects this is an interface
- Uses `InterfaceInterceptor` (no `@SuperCall` needed)
- Returns mock value or throws exception if not configured

## Usage Examples

### Example 1: Dubbo Service Interface

```java
import org.apache.dubbo.config.annotation.Service;

@Service
public interface OrderService {
    OrderDTO getOrderById(Long orderId);
    List<OrderDTO> getOrdersByUserId(Long userId);
    boolean cancelOrder(Long orderId);
}
```

**Mock Configuration:**

1. Right-click on `getOrderById` method
2. Select "Add Mock for Method"
3. Configure return value:
```json
{
  "orderId": 12345,
  "userId": 100,
  "amount": 99.99,
  "status": "COMPLETED"
}
```

### Example 2: Feign Client Interface

```java
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.*;

@FeignClient(name = "user-service", url = "http://localhost:8080")
public interface UserClient {
    
    @GetMapping("/users/{id}")
    UserDTO getUser(@PathVariable("id") Long id);
    
    @PostMapping("/users")
    UserDTO createUser(@RequestBody UserDTO user);
    
    @DeleteMapping("/users/{id}")
    void deleteUser(@PathVariable("id") Long id);
}
```

**Mock Configuration:**

For `getUser`:
```json
{
  "id": 1,
  "username": "testuser",
  "email": "test@example.com",
  "role": "ADMIN"
}
```

For `createUser`:
```json
{
  "id": 999,
  "username": "newuser",
  "email": "new@example.com",
  "role": "USER"
}
```

### Example 3: Mock Exception for Error Testing

Test how your code handles Feign client errors:

1. Select the method in Mock Runner tool window
2. Change Mode to "Exception"
3. Configure:
   - Exception Type: `feign.FeignException$ServiceUnavailable`
   - Message: `Service temporarily unavailable`

Or use a generic exception:
   - Exception Type: `java.lang.RuntimeException`
   - Message: `Connection timeout`

## Testing Workflow

### Step 1: Create Test Interface

```java
public interface PaymentServiceApi {
    PaymentResult processPayment(PaymentRequest request);
}
```

### Step 2: Configure Mock

In Mock Runner tool window:
- Class: `com.example.PaymentServiceApi`
- Method: `processPayment`
- Return Value:
```json
{
  "transactionId": "TXN-12345",
  "status": "SUCCESS",
  "amount": 100.00
}
```

### Step 3: Run Your Test

```java
public class PaymentTest {
    public static void main(String[] args) {
        PaymentServiceApi service = createDubboProxy(); // or Feign proxy
        
        PaymentRequest request = new PaymentRequest(100.00);
        PaymentResult result = service.processPayment(request);
        
        System.out.println("Transaction: " + result.getTransactionId());
        System.out.println("Status: " + result.getStatus());
    }
}
```

Just use standard Run/Debug - the agent is automatically injected!

## Common Use Cases

### 1. Local Development Without Remote Services

Mock Dubbo services so you can develop locally without running the entire microservices cluster:

```java
@Service
public interface InventoryService {
    int getStock(String productId);
}
```

Mock return: `100` (as integer)

### 2. Integration Testing

Test different response scenarios without hitting real APIs:

```java
@FeignClient("order-service")
public interface OrderClient {
    Order getOrder(String orderId);
}
```

Mock different scenarios:
- Success: `{"orderId":"123","status":"COMPLETED"}`
- Not found: Exception mode with `java.lang.IllegalArgumentException`

### 3. Error Handling Testing

Test how your code handles various exceptions:

```java
@Service
public interface NotificationService {
    void sendEmail(String to, String subject, String body);
}
```

Mock exception:
- Type: `java.io.IOException`
- Message: `SMTP server connection failed`

## Troubleshooting

### Mock Not Working?

1. **Verify interface detection**: Check IDE logs for `"(interface: true)"`
2. **Check method signature**: Must match exactly including package name
3. **Enable the mock**: Ensure checkbox is checked in Mock Runner tool window

### UnsupportedOperationException?

This means:
- The method was called
- No mock is configured
- The interface has no implementation

Solution: Add mock configuration in Mock Runner tool window

### ClassCastException?

Ensure your mock JSON matches the return type:
- For `User`: Use object `{...}`
- For `List<User>`: Use array `[{...}, {...}]`
- For primitives: Use plain values `123`, `true`, `"text"`

## Best Practices

1. **Use realistic mock data**: Match your production data structure
2. **Test both success and error cases**: Use both return value and exception modes
3. **Keep mocks in version control**: Commit `.idea/mockRunnerConfig.xml`
4. **Document mock scenarios**: Add comments explaining what each mock tests
5. **Clean up unused mocks**: Remove mocks that are no longer needed

## Technical Details

### How Interface Detection Works

```java
// In MockAgent.java
boolean isInterface = typeDescription.isInterface();

if (isInterface) {
    // Use InterfaceInterceptor (no SuperCall)
    builder = builder.method(ElementMatchers.named(methodName))
        .intercept(MethodDelegation.to(InterfaceInterceptor.class));
} else {
    // Use regular Interceptor (with SuperCall)
    builder = builder.method(ElementMatchers.named(methodName))
        .intercept(MethodDelegation.to(Interceptor.class));
}
```

### InterfaceInterceptor vs Regular Interceptor

**InterfaceInterceptor:**
- No `@SuperCall` parameter
- Must return mock value or throw exception
- Used for interfaces (Dubbo/Feign)

**Regular Interceptor:**
- Has `@SuperCall` parameter
- Can fall back to original implementation
- Used for concrete classes

## Examples in This Project

See `src/test/InterfaceMockTest.java` for a complete working example that demonstrates:
- Creating a dynamic proxy (simulating Dubbo/Feign)
- Calling interface methods
- Handling mocked responses
- Error scenarios

Run it with standard Run/Debug to see interface mocking in action!

## Support

If you encounter issues with Dubbo/Feign mocking:
1. Check the IDE logs for agent messages
2. Verify the interface is being detected correctly
3. Ensure mock configuration matches method signature
4. Report issues at: https://github.com/lancelothuxi/mock-runner-plugin/issues
