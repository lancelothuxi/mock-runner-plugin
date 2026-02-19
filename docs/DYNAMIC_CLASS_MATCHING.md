# Dynamic Class Matching Implementation

## Overview

The MockAgent now dynamically extracts class names from the mock configuration instead of using hardcoded package prefixes. This makes the agent flexible and able to work with any package structure.

## Problem with Previous Implementation

### Before (Hardcoded)

```java
.type(ElementMatchers.nameStartsWith("test."))
```

**Issues:**
- Only worked with `test.*` package
- Required code changes to support different packages
- Not flexible for real-world usage
- Users couldn't use their own package names

## New Implementation (Dynamic)

### How It Works

1. **Parse Mock Configuration**: Read all mock rules from config file
2. **Extract Class Names**: Parse each rule key (e.g., `com.example.Service.method`) and extract the class name (`com.example.Service`)
3. **Build Dynamic Matcher**: Create ElementMatcher that only targets classes with configured mocks
4. **Install Agent**: ByteBuddy transforms only the specified classes

### Code Implementation

```java
// Extract unique class names from mock rules
java.util.Set<String> classesToMock = new java.util.HashSet<>();
for (Map.Entry<String, MockConfig.MockRule> entry : mockConfig.getAllRules().entrySet()) {
    String key = entry.getKey();  // e.g., "com.example.OrderService.getOrder"
    
    // Extract class name from "com.example.ClassName.methodName"
    int lastDot = key.lastIndexOf('.');
    if (lastDot > 0) {
        String className = key.substring(0, lastDot);  // "com.example.OrderService"
        classesToMock.add(className);
    }
}

LOG.info("[MockAgent] Classes to intercept: " + classesToMock);

// Build type matcher for classes that have mock rules
net.bytebuddy.matcher.ElementMatcher.Junction<TypeDescription> typeMatcher = null;
for (String className : classesToMock) {
    if (typeMatcher == null) {
        typeMatcher = ElementMatchers.named(className);
    } else {
        typeMatcher = typeMatcher.or(ElementMatchers.named(className));
    }
}

// Install agent with dynamic matcher
new AgentBuilder.Default()
    .ignore(/* framework classes */)
    .type(typeMatcher)  // Dynamic matcher!
    .transform(/* ... */)
    .installOn(inst);
```

## Benefits

### 1. Package Flexibility

Works with ANY package name:

```json
{
  "mockRules": {
    "com.mycompany.service.OrderService.getOrder": { ... },
    "org.example.api.UserClient.getUser": { ... },
    "io.github.project.PaymentService.pay": { ... }
  }
}
```

### 2. Efficiency

Only transforms classes that actually need mocking:

```
[MockAgent] Classes to intercept: [test.feign.FeignUserClient, test.dubbo.DubboOrderService]
```

Instead of transforming ALL classes in a package.

### 3. No Code Changes Required

Users can use their existing package structure without modifying the agent code.

### 4. Clear Logging

Agent logs exactly which classes will be intercepted:

```
[MockAgent] Loaded 11 mock rules
[MockAgent]   - test.dubbo.DubboOrderService.getOrderById -> ...
[MockAgent]   - test.dubbo.DubboOrderService.getOrdersByUserId -> ...
[MockAgent]   - test.feign.FeignUserClient.getUser -> ...
[MockAgent] Classes to intercept: [test.feign.FeignUserClient, test.dubbo.DubboOrderService]
[MockAgent] *** Intercepting test.dubbo.DubboOrderService.getOrderById (interface: true) ***
[MockAgent] *** Intercepting test.feign.FeignUserClient.getUser (interface: true) ***
```

## Example Usage

### Configuration File

```json
{
  "mockRules": {
    "com.example.dubbo.OrderService.createOrder": {
      "returnValue": "{\"orderId\":123,\"status\":\"SUCCESS\"}",
      "returnType": "com.example.dto.OrderDTO",
      "enabled": true
    },
    "com.example.feign.UserClient.getUser": {
      "returnValue": "{\"id\":1,\"name\":\"John\"}",
      "returnType": "com.example.dto.UserDTO",
      "enabled": true
    },
    "org.myapp.payment.PaymentService.processPayment": {
      "returnValue": "true",
      "returnType": "boolean",
      "enabled": true
    }
  }
}
```

### Agent Output

```
[MockAgent] Classes to intercept: [
  com.example.dubbo.OrderService,
  com.example.feign.UserClient,
  org.myapp.payment.PaymentService
]
[MockAgent] *** Intercepting com.example.dubbo.OrderService.createOrder (interface: true) ***
[MockAgent] *** Intercepting com.example.feign.UserClient.getUser (interface: true) ***
[MockAgent] *** Intercepting org.myapp.payment.PaymentService.processPayment (interface: false) ***
```

## Technical Details

### Class Name Extraction

```java
String key = "com.example.OrderService.getOrder";
int lastDot = key.lastIndexOf('.');  // Find last dot
String className = key.substring(0, lastDot);  // "com.example.OrderService"
```

### Type Matcher Building

```java
// Start with null
ElementMatcher.Junction<TypeDescription> typeMatcher = null;

// Add first class
typeMatcher = ElementMatchers.named("com.example.OrderService");

// Add more classes with OR
typeMatcher = typeMatcher.or(ElementMatchers.named("com.example.UserClient"));
typeMatcher = typeMatcher.or(ElementMatchers.named("org.myapp.PaymentService"));

// Result: matches any of the specified classes
```

### Safety Check

```java
if (typeMatcher == null) {
    LOG.warning("[MockAgent] No classes to mock, agent not installed");
    return;
}
```

If no valid mock rules exist, the agent doesn't install (avoids unnecessary overhead).

## Comparison

| Feature | Hardcoded Approach | Dynamic Approach |
|---------|-------------------|------------------|
| Package flexibility | ❌ Only `test.*` | ✅ Any package |
| Code changes needed | ❌ Yes, for each package | ✅ No changes needed |
| Performance | ⚠️ Transforms all `test.*` classes | ✅ Only transforms configured classes |
| User-friendly | ❌ Requires code modification | ✅ Just configure JSON |
| Logging clarity | ⚠️ Generic | ✅ Shows exact classes |
| Real-world usage | ❌ Limited | ✅ Production-ready |

## Testing

### Test Execution

```bash
./gradlew-java17.sh test --info 2>&1 | grep "Classes to intercept"
```

### Expected Output

```
[MockAgent] Classes to intercept: [test.feign.FeignUserClient, test.dubbo.DubboOrderService]
```

### Verification

All configured classes are listed, and agent logs show interception for each method:

```
[MockAgent] *** Intercepting test.dubbo.DubboOrderService.getOrderById (interface: true) ***
[MockAgent] *** Intercepting test.dubbo.DubboOrderService.getOrdersByUserId (interface: true) ***
[MockAgent] *** Intercepting test.dubbo.DubboOrderService.createOrder (interface: true) ***
...
```

## Migration Guide

If you have existing mock configurations with hardcoded package assumptions, no changes are needed! The dynamic approach automatically detects your package structure from the configuration.

### Example Migration

**Old config** (worked only with `test.*`):
```json
{
  "mockRules": {
    "test.OrderService.getOrder": { ... }
  }
}
```

**New config** (works with any package):
```json
{
  "mockRules": {
    "com.mycompany.OrderService.getOrder": { ... },
    "org.example.UserService.getUser": { ... },
    "io.github.PaymentService.pay": { ... }
  }
}
```

Both formats work! The agent dynamically extracts class names from whatever you configure.

## Future Enhancements

### Potential Improvements

1. **Wildcard Support**: `com.example.*.Service` to match multiple classes
2. **Package-Level Mocking**: `com.example.service.*` to mock entire packages
3. **Regex Patterns**: More flexible matching patterns
4. **Configuration Validation**: Warn if class names are invalid

### Current Limitations

- Requires exact class name in configuration
- No wildcard or pattern matching (yet)
- Single-level class name extraction (doesn't handle nested classes specially)

## Conclusion

The dynamic class matching implementation makes the MockAgent:

✅ **Flexible**: Works with any package structure  
✅ **Efficient**: Only transforms necessary classes  
✅ **User-friendly**: No code changes required  
✅ **Production-ready**: Suitable for real-world projects  
✅ **Maintainable**: Configuration-driven, not code-driven  

This is a significant improvement over the hardcoded approach and makes the plugin truly reusable across different projects and package structures.
