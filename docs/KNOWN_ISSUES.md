# Known Issues and Limitations

## Dynamic Proxy Limitation

### Issue

The current test implementation uses `java.lang.reflect.Proxy` to create dynamic proxies for Dubbo/Feign interfaces. However, ByteBuddy agent cannot intercept these proxies because:

1. Proxies are created AFTER the agent is installed
2. ByteBuddy transforms classes at load time, not at creation time
3. Proxy classes have generated names like `$Proxy0`, not the interface name

### Current Behavior

```java
// This creates a proxy AFTER agent installation
DubboOrderService service = (DubboOrderService) Proxy.newProxyInstance(...);

// Agent cannot intercept this call
service.getOrderById(123L); // Throws UnsupportedOperationException
```

### Solutions

#### Solution 1: Use Concrete Test Classes (Recommended for Unit Tests)

Instead of dynamic proxies, create simple test implementations:

```java
public class DubboOrderServiceTestImpl implements DubboOrderService {
    @Override
    public OrderDTO getOrderById(Long orderId) {
        // This will be intercepted by agent
        return null; // Agent returns mock value
    }
}
```

#### Solution 2: Use Real Dubbo/Feign Framework (Integration Tests)

For integration tests, use the actual Dubbo or Feign framework which creates proxies before tests run:

```java
@DubboReference
private DubboOrderService orderService; // Real Dubbo proxy

@Test
public void test() {
    // This works because Dubbo proxy is created by framework
    orderService.getOrderById(123L);
}
```

#### Solution 3: Manual Agent Attachment (Advanced)

Attach agent programmatically after JVM starts but before creating proxies:

```java
@BeforeClass
public static void attachAgent() {
    ByteBuddyAgent.install();
    // Then install our mock agent
}
```

### Workaround for Current Tests

The tests are designed to demonstrate the plugin's capability, but they need to be run in an actual IDE with the plugin installed, not as standalone unit tests.

**To test the plugin:**

1. Build the plugin: `./gradlew-java17.sh buildPlugin`
2. Install in IntelliJ: `Settings → Plugins → Install from Disk`
3. Create a real project with Dubbo/Feign
4. Use the Mock Runner tool window to configure mocks
5. Run/Debug your application - mocks will work!

## Gradle Serialization Issue (FIXED)

### Issue

```
java.io.InvalidClassException: org.gradle.api.internal.tasks.testing.testng.TestNgTestClassProcessorFactory
```

### Solution

Added class filtering to agent to ignore Gradle internal classes:

```java
.ignore(ElementMatchers.nameStartsWith("org.gradle."))
.ignore(ElementMatchers.nameStartsWith("org.testng."))
// ... other framework classes
```

## Java Version Requirement

### Issue

```
No matching variant... compatible with Java 8
```

### Solution

Use Java 17:

```bash
./gradlew-java17.sh test
```

Or set JAVA_HOME:

```bash
export JAVA_HOME=/usr/lib/jvm/java-17-openjdk-amd64
./gradlew test
```

## Test Execution Recommendations

### For Plugin Development

1. **Build the plugin**: `./gradlew-java17.sh buildPlugin`
2. **Install in IDE**: Load the plugin ZIP
3. **Test in real project**: Create actual Dubbo/Feign project
4. **Use Mock Runner UI**: Configure mocks via tool window
5. **Run with IDE**: Use standard Run/Debug buttons

### For Unit Testing

Current unit tests are **demonstration/documentation** purposes. They show:
- How to structure Dubbo/Feign interfaces
- What mock configurations look like
- How the agent should work

For actual testing, use the plugin in a real IDE environment.

## Future Improvements

### Planned

- [ ] Support for programmatic agent attachment
- [ ] Better proxy detection and interception
- [ ] Standalone test runner that doesn't use Gradle
- [ ] Mock configuration validation tool
- [ ] Integration test examples with real Dubbo/Feign

### Contributions Welcome

If you have ideas for solving the dynamic proxy limitation, please open an issue or PR!

## Getting Help

- Check [TESTNG_GUIDE.md](TESTNG_GUIDE.md) for test setup
- Check [DUBBO_FEIGN_GUIDE.md](DUBBO_FEIGN_GUIDE.md) for usage examples
- Check [BUILD_INSTRUCTIONS.md](BUILD_INSTRUCTIONS.md) for build help
- Open an issue: https://github.com/lancelothuxi/mock-runner-plugin/issues
