# Test Resources

## Mock Configuration

### mock-config-test.json

This file contains all mock configurations for automated tests. The Gradle test task automatically:

1. Builds the mock agent JAR
2. Attaches the agent to the test JVM with `-javaagent`
3. Loads this configuration file

### How It Works

```
Gradle Test Task
  ↓
Build Agent JAR (agentJar task)
  ↓
Start Test JVM with:
  -javaagent:build/libs/mock-agent-1.0.6-agent.jar=src/test/resources/mock-config-test.json
  ↓
Agent loads mock-config-test.json
  ↓
Agent intercepts interface method calls
  ↓
Tests run with mocked responses
```

### Configuration Format

```json
{
  "mockRules": {
    "fully.qualified.ClassName.methodName": {
      "returnValue": "JSON string of return value",
      "returnType": "fully.qualified.ReturnType",
      "enabled": true,
      "throwException": false
    }
  }
}
```

### Adding New Mocks

To add a new mock for testing:

1. Add entry to `mockRules` in `mock-config-test.json`
2. Use format: `"package.ClassName.methodName": { ... }`
3. Provide JSON string for `returnValue`
4. Specify full type for `returnType`

Example:
```json
"test.dubbo.DubboOrderService.getOrderById": {
  "returnValue": "{\"orderId\":12345,\"status\":\"COMPLETED\"}",
  "returnType": "test.dubbo.OrderDTO",
  "enabled": true,
  "throwException": false
}
```

### Exception Mocking

To test exception scenarios:

```json
"test.dubbo.DubboOrderService.getOrderById": {
  "returnValue": "",
  "returnType": "test.dubbo.OrderDTO",
  "enabled": true,
  "throwException": true,
  "exceptionType": "java.lang.RuntimeException",
  "exceptionMessage": "Service unavailable"
}
```

Then enable the exception test in the test class:
```java
@Test(enabled = true)  // Change from false to true
public void testServiceException() {
    // Test code
}
```

## TestNG Configuration

### testng.xml

Defines the test suite structure:

```xml
<suite name="Mock Runner Test Suite">
    <test name="Dubbo Interface Mock Tests">
        <classes>
            <class name="test.dubbo.DubboServiceTest"/>
        </classes>
    </test>
    <test name="Feign Client Mock Tests">
        <classes>
            <class name="test.feign.FeignClientTest"/>
        </classes>
    </test>
</suite>
```

## Running Tests

### From Command Line

```bash
# Run all tests with agent
./gradlew-java17.sh test

# Run specific test class
./gradlew-java17.sh test --tests "test.dubbo.DubboServiceTest"

# Run with verbose output
./gradlew-java17.sh test --info
```

### From IDE

1. Right-click on test class or method
2. Select "Run 'TestName'"
3. Agent will NOT be attached automatically in IDE
4. You need to manually add VM options:
   ```
   -javaagent:build/libs/mock-agent-1.0.6-agent.jar=src/test/resources/mock-config-test.json
   ```

### Verifying Agent is Loaded

Check test output for:
```
✓ Mock Agent attached: /path/to/mock-agent-1.0.6-agent.jar
✓ Mock Config: /path/to/mock-config-test.json
```

If you see:
```
⚠ Warning: Agent or config not found
```

Then run:
```bash
./gradlew-java17.sh agentJar  # Build agent first
./gradlew-java17.sh test      # Then run tests
```

## Troubleshooting

### Tests Fail with UnsupportedOperationException

**Problem**: Agent not attached or mock not configured

**Solution**:
1. Verify agent JAR exists: `ls build/libs/mock-agent-*.jar`
2. Verify config exists: `cat src/test/resources/mock-config-test.json`
3. Rebuild agent: `./gradlew-java17.sh agentJar`
4. Run tests: `./gradlew-java17.sh test`

### Agent Attached but Mocks Not Working

**Problem**: Mock configuration doesn't match method signature

**Solution**:
1. Check class name is fully qualified: `test.dubbo.DubboOrderService`
2. Check method name matches exactly
3. Check return type is correct
4. Verify `enabled: true`

### ClassCastException in Tests

**Problem**: Return type doesn't match mock value

**Solution**:
1. For objects: Use JSON object `{...}`
2. For lists: Use JSON array `[{...}, {...}]`
3. For primitives: Use plain value `"5"`, `"true"`
4. For void: Use empty string `""`

## Mock Configuration Reference

All currently configured mocks:

### Dubbo Tests
- `DubboOrderService.getOrderById` → OrderDTO
- `DubboOrderService.getOrdersByUserId` → List<OrderDTO>
- `DubboOrderService.createOrder` → OrderDTO
- `DubboOrderService.cancelOrder` → boolean
- `DubboOrderService.getOrderCount` → int

### Feign Tests
- `FeignUserClient.getUser` → UserResponse
- `FeignUserClient.getAllUsers` → List<UserResponse>
- `FeignUserClient.createUser` → UserResponse
- `FeignUserClient.updateUser` → UserResponse
- `FeignUserClient.deleteUser` → void
- `FeignUserClient.searchUsers` → List<UserResponse>

## CI/CD Integration

The test task is configured to automatically attach the agent, so CI/CD pipelines work out of the box:

```yaml
# GitHub Actions
- name: Run tests
  run: ./gradlew test
```

No additional configuration needed!
