# Interface Mocking Feature Summary

## What's New in v1.0.6

Mock Runner now fully supports **interface-based RPC frameworks** like Dubbo and Feign!

## The Problem

Dubbo and Feign use interface-based programming:
```java
// No implementation class!
public interface UserService {
    User getUser(String id);
}
```

Previous versions couldn't mock these because:
- No concrete implementation to intercept
- `@SuperCall` requires an actual method body
- Dynamic proxies are created at runtime

## The Solution

We added intelligent interface detection:

```java
boolean isInterface = typeDescription.isInterface();

if (isInterface) {
    // Use InterfaceInterceptor - no SuperCall needed
    builder = builder.method(ElementMatchers.named(methodName))
        .intercept(MethodDelegation.to(InterfaceInterceptor.class));
} else {
    // Use regular Interceptor - with SuperCall for fallback
    builder = builder.method(ElementMatchers.named(methodName))
        .intercept(MethodDelegation.to(Interceptor.class));
}
```

## Two Interceptor Types

### 1. Regular Interceptor (for concrete classes)
```java
@RuntimeType
public static Object intercept(@Origin Method method,
                                @AllArguments Object[] args,
                                @SuperCall Callable<?> zuper) {
    // Can call zuper.call() to execute original method
}
```

### 2. InterfaceInterceptor (for interfaces)
```java
@RuntimeType
public static Object intercept(@Origin Method method,
                                @AllArguments Object[] args) {
    // No SuperCall - must return mock or throw exception
}
```

## Usage Example

```java
// 1. Define Dubbo/Feign interface
@Service
public interface OrderService {
    Order getOrder(String orderId);
}

// 2. Configure mock in Mock Runner tool window
// Class: com.example.OrderService
// Method: getOrder
// Return Value: {"orderId":"123","status":"COMPLETED"}

// 3. Run your application with standard Run/Debug
OrderService service = getDubboProxy();
Order order = service.getOrder("123");
// Returns mocked value!
```

## Key Benefits

✅ **No implementation needed** - Mock pure interfaces  
✅ **Works with Dubbo** - Mock remote service calls  
✅ **Works with Feign** - Mock HTTP client calls  
✅ **Exception support** - Test error scenarios  
✅ **Zero code changes** - Just configure and run  

## Test It Yourself

Run `src/test/InterfaceMockTest.java` to see it in action!

## Technical Details

- Uses ByteBuddy's `typeDescription.isInterface()` for detection
- InterfaceInterceptor throws `UnsupportedOperationException` if no mock configured
- Both interceptors share the same `parseMockValue()` logic
- Exception mode works for both interface and class methods

## Documentation

- Full guide: `docs/DUBBO_FEIGN_GUIDE.md`
- Examples: `src/test/InterfaceMockTest.java`
- README: Updated with Dubbo/Feign section

## Version Info

- Version: 1.0.6
- Release Date: 2026-02-16
- Commit: feat: Add full support for Dubbo and Feign interface-based RPC calls
