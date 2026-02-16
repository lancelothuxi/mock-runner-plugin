# TestNG Test Guide for Mock Runner

This guide explains how to run and configure TestNG tests for Dubbo and Feign interface mocking.

## Overview

All tests are written using TestNG framework with proper assertions and test lifecycle management.

## Test Structure

```
src/test/
├── java/test/                          # Test source (Maven standard)
│   ├── dubbo/
│   │   ├── DubboOrderService.java      # Dubbo service interface
│   │   ├── OrderDTO.java               # Order data transfer object
│   │   ├── CreateOrderRequest.java     # Request object
│   │   └── DubboServiceTest.java       # TestNG test class
│   └── feign/
│       ├── FeignUserClient.java        # Feign client interface
│       ├── UserResponse.java           # User response DTO
│       ├── CreateUserRequest.java      # Create user request
│       ├── UpdateUserRequest.java      # Update user request
│       └── FeignClientTest.java        # TestNG test class
└── resources/                          # Test resources
    └── testng.xml                      # TestNG suite configuration
```

## Running Tests

### Method 1: Run Individual Test Class

1. Open `DubboServiceTest.java` or `FeignClientTest.java`
2. Right-click on the class
3. Select "Run 'DubboServiceTest'" or "Run 'FeignClientTest'"
4. IntelliJ will automatically use TestNG

### Method 2: Run Entire Test Suite

1. Right-click on `src/test/resources/testng.xml`
2. Select "Run 'testng.xml'"
3. All tests will run in sequence

### Method 3: Run from Command Line

```bash
# Using Gradle
./gradlew test

# Run specific test class
./gradlew test --tests "test.dubbo.DubboServiceTest"
./gradlew test --tests "test.feign.FeignClientTest"
```

## Configuring Mocks

Before running tests, you MUST configure mocks in the Mock Runner tool window.

### Dubbo Test Mock Configurations

#### 1. getOrderById
```
Class: test.dubbo.DubboOrderService
Method: getOrderById
Return Type: test.dubbo.OrderDTO
Return Value:
{
  "orderId": 12345,
  "userId": 100,
  "orderNo": "ORD-2026-001",
  "totalAmount": 299.99,
  "status": "COMPLETED"
}
```

#### 2. getOrdersByUserId
```
Class: test.dubbo.DubboOrderService
Method: getOrdersByUserId
Return Type: java.util.List<test.dubbo.OrderDTO>
Return Value:
[
  {
    "orderId": 12345,
    "userId": 100,
    "orderNo": "ORD-2026-001",
    "totalAmount": 299.99,
    "status": "COMPLETED"
  },
  {
    "orderId": 12346,
    "userId": 100,
    "orderNo": "ORD-2026-002",
    "totalAmount": 599.99,
    "status": "SHIPPED"
  }
]
```

#### 3. createOrder
```
Class: test.dubbo.DubboOrderService
Method: createOrder
Return Type: test.dubbo.OrderDTO
Return Value:
{
  "orderId": 99999,
  "userId": 100,
  "orderNo": "ORD-2026-NEW",
  "totalAmount": 199.99,
  "status": "PENDING"
}
```

#### 4. cancelOrder
```
Class: test.dubbo.DubboOrderService
Method: cancelOrder
Return Type: boolean
Return Value: true
```

#### 5. getOrderCount
```
Class: test.dubbo.DubboOrderService
Method: getOrderCount
Return Type: int
Return Value: 5
```

### Feign Test Mock Configurations

#### 1. getUser
```
Class: test.feign.FeignUserClient
Method: getUser
Return Type: test.feign.UserResponse
Return Value:
{
  "id": 1,
  "username": "john_doe",
  "email": "john@example.com",
  "phone": "+1234567890",
  "role": "ADMIN",
  "status": "ACTIVE"
}
```

#### 2. getAllUsers
```
Class: test.feign.FeignUserClient
Method: getAllUsers
Return Type: java.util.List<test.feign.UserResponse>
Return Value:
[
  {
    "id": 1,
    "username": "john_doe",
    "email": "john@example.com",
    "phone": "+1234567890",
    "role": "ADMIN",
    "status": "ACTIVE"
  },
  {
    "id": 2,
    "username": "jane_smith",
    "email": "jane@example.com",
    "phone": "+0987654321",
    "role": "USER",
    "status": "ACTIVE"
  }
]
```

#### 3. createUser
```
Class: test.feign.FeignUserClient
Method: createUser
Return Type: test.feign.UserResponse
Return Value:
{
  "id": 999,
  "username": "new_user",
  "email": "newuser@example.com",
  "phone": "+1111111111",
  "role": "USER",
  "status": "ACTIVE"
}
```

#### 4. updateUser
```
Class: test.feign.FeignUserClient
Method: updateUser
Return Type: test.feign.UserResponse
Return Value:
{
  "id": 1,
  "username": "john_doe",
  "email": "john.updated@example.com",
  "phone": "+9999999999",
  "role": "SUPER_ADMIN",
  "status": "ACTIVE"
}
```

#### 5. deleteUser
```
Class: test.feign.FeignUserClient
Method: deleteUser
Return Type: void
Return Value: (leave empty for void methods)
```

#### 6. searchUsers
```
Class: test.feign.FeignUserClient
Method: searchUsers
Return Type: java.util.List<test.feign.UserResponse>
Return Value:
[
  {
    "id": 1,
    "username": "john_doe",
    "email": "john@example.com",
    "phone": "+1234567890",
    "role": "ADMIN",
    "status": "ACTIVE"
  }
]
```

## Test Features

### TestNG Annotations Used

- `@BeforeClass`: Setup method run once before all tests
- `@AfterClass`: Teardown method run once after all tests
- `@Test`: Marks a method as a test
- `@Test(priority = N)`: Controls test execution order
- `@Test(description = "...")`: Adds test description
- `@Test(enabled = false)`: Disables a test

### Assertions

All tests use TestNG assertions:

```java
Assert.assertNotNull(object, "Message");
Assert.assertEquals(actual, expected, "Message");
Assert.assertTrue(condition, "Message");
Assert.assertThrows(ExceptionClass.class, () -> { ... });
```

### Test Output

Tests produce formatted console output:

```
╔════════════════════════════════════════════════════════════╗
║         Dubbo Service Interface Mock Test (TestNG)         ║
╚════════════════════════════════════════════════════════════╝

━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━
Test 1: getOrderById(12345L)
━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━
✅ SUCCESS
   Order ID: 12345
   Order No: ORD-2026-001
   Amount: $299.99
   Status: COMPLETED
```

## Exception Testing

Both test classes include disabled exception tests. To enable:

1. Find the test method with `enabled = false`
2. Change to `enabled = true`
3. Configure exception mock in Mock Runner:
   - Mode: Exception
   - Exception Type: `java.lang.RuntimeException`
   - Message: Your error message

Example for Dubbo:
```java
@Test(priority = 6, description = "Test exception handling", enabled = true)
public void testServiceException() {
    Assert.assertThrows(RuntimeException.class, () -> {
        orderService.getOrderById(12345L);
    });
}
```

## Troubleshooting

### Tests Fail with UnsupportedOperationException

**Problem**: Mock not configured

**Solution**: 
1. Open Mock Runner tool window
2. Add mock configuration for the failing method
3. Ensure mock is enabled (checkbox checked)
4. Re-run tests

### Tests Pass but Assertions Fail

**Problem**: Mock return value doesn't match expected value

**Solution**:
1. Check mock configuration in Mock Runner
2. Verify JSON format is correct
3. Ensure return type matches method signature
4. Update mock value to match test expectations

### TestNG Not Found

**Problem**: TestNG dependency not loaded

**Solution**:
```bash
# Refresh Gradle dependencies
./gradlew clean build --refresh-dependencies
```

### Agent Not Loading

**Problem**: Mock agent not injected

**Solution**:
1. Verify plugin is installed
2. Check Mock Runner tool window shows configurations
3. Use standard Run/Debug (not custom run configurations)
4. Check IDE logs for agent loading messages

## Best Practices

1. **Configure all mocks before running tests**
   - Tests will fail if mocks are not configured
   - Use the mock configurations provided in this guide

2. **Run tests in order**
   - Tests use `priority` to ensure correct execution order
   - Don't change priorities unless necessary

3. **Check test output**
   - Tests print detailed information about what they're testing
   - Use output to debug issues

4. **Enable exception tests separately**
   - Exception tests are disabled by default
   - Enable them when you want to test error scenarios
   - Remember to configure exception mocks

5. **Use TestNG suite for full testing**
   - Run `testng.xml` to execute all tests
   - Individual test classes can be run separately

## CI/CD Integration

### GitHub Actions Example

```yaml
name: Run Tests

on: [push, pull_request]

jobs:
  test:
    runs-on: ubuntu-latest
    steps:
      - uses: actions/checkout@v2
      - name: Set up JDK 17
        uses: actions/setup-java@v2
        with:
          java-version: '17'
      - name: Run tests
        run: ./gradlew test
      - name: Publish test results
        uses: EnricoMi/publish-unit-test-result-action@v2
        if: always()
        with:
          files: build/test-results/**/*.xml
```

## Additional Resources

- TestNG Documentation: https://testng.org/doc/documentation-main.html
- Mock Runner Guide: `docs/DUBBO_FEIGN_GUIDE.md`
- Architecture: `docs/ARCHITECTURE_DIAGRAM.md`
