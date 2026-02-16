# Mock Runner Test Suite

Comprehensive TestNG tests demonstrating Dubbo and Feign interface mocking.

## Directory Structure (Maven Standard)

```
src/test/
├── java/test/                          # Test source files
│   ├── dubbo/                          # Dubbo RPC tests
│   │   ├── DubboOrderService.java
│   │   ├── OrderDTO.java
│   │   ├── CreateOrderRequest.java
│   │   └── DubboServiceTest.java
│   ├── feign/                          # Feign HTTP client tests
│   │   ├── FeignUserClient.java
│   │   ├── UserResponse.java
│   │   ├── CreateUserRequest.java
│   │   ├── UpdateUserRequest.java
│   │   └── FeignClientTest.java
│   ├── InterfaceMockTest.java          # Generic interface test
│   ├── User.java                       # Test entities
│   ├── UserServiceApi.java
│   ├── Student.java
│   ├── TestMain.java
│   └── TestService.java
├── resources/                          # Test resources
│   └── testng.xml                      # TestNG suite configuration
└── README.md                           # This file
```

## Quick Start

### 1. Configure Mocks

Open Mock Runner tool window and add mock configurations (see test files for exact JSON).

### 2. Run Tests

**Option A: Run entire suite**
```bash
./gradlew test
```

**Option B: Run specific test**
- Right-click on `DubboServiceTest.java` or `FeignClientTest.java`
- Select "Run 'DubboServiceTest'"

**Option C: Run from testng.xml**
- Right-click on `src/test/resources/testng.xml`
- Select "Run 'testng.xml'"

## Test Structure

### Dubbo Tests (`test.dubbo`)

Tests for Dubbo RPC service interface mocking:

- **DubboOrderService**: Interface definition (simulates @Service)
- **OrderDTO**: Order data transfer object
- **CreateOrderRequest**: Complex request object with nested items
- **DubboServiceTest**: 6 TestNG test cases

**Test Cases:**
1. `testGetOrderById` - Single object return
2. `testGetOrdersByUserId` - List return
3. `testCreateOrder` - Complex object creation
4. `testCancelOrder` - Boolean return
5. `testGetOrderCount` - Primitive int return
6. `testServiceException` - Exception handling (disabled)

### Feign Tests (`test.feign`)

Tests for Feign HTTP client interface mocking:

- **FeignUserClient**: Interface definition (simulates @FeignClient)
- **UserResponse**: User response DTO
- **CreateUserRequest**: Create user request
- **UpdateUserRequest**: Update user request
- **FeignClientTest**: 7 TestNG test cases

**Test Cases:**
1. `testGetUser` - GET /api/users/{id}
2. `testGetAllUsers` - GET /api/users
3. `testCreateUser` - POST /api/users
4. `testUpdateUser` - PUT /api/users/{id}
5. `testDeleteUser` - DELETE /api/users/{id} (void)
6. `testSearchUsers` - GET /api/users/search
7. `testUserNotFound` - 404 exception (disabled)

## Mock Configuration Examples

### Dubbo Example

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

### Feign Example

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

## Test Output

Tests produce formatted output:

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

## Features

- ✅ TestNG framework with proper annotations
- ✅ Ordered test execution with `@Test(priority = N)`
- ✅ Comprehensive assertions
- ✅ Exception testing support
- ✅ Detailed console output
- ✅ Mock configuration instructions in comments
- ✅ Simulates real Dubbo/Feign proxy behavior

## Documentation

- **Complete Guide**: `docs/TESTNG_GUIDE.md`
- **Dubbo/Feign Guide**: `docs/DUBBO_FEIGN_GUIDE.md`
- **Architecture**: `docs/ARCHITECTURE_DIAGRAM.md`

## Troubleshooting

**Tests fail with UnsupportedOperationException?**
→ Configure mocks in Mock Runner tool window

**TestNG not found?**
→ Run `./gradlew clean build --refresh-dependencies`

**Agent not loading?**
→ Use standard Run/Debug buttons (not custom configurations)

## CI/CD

Tests can be integrated into CI/CD pipelines:

```yaml
- name: Run tests
  run: ./gradlew test
- name: Publish test results
  uses: EnricoMi/publish-unit-test-result-action@v2
  with:
    files: build/test-results/**/*.xml
```
