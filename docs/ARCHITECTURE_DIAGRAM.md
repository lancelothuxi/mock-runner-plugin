# Mock Runner Architecture

## How Interface Mocking Works

```
┌─────────────────────────────────────────────────────────────┐
│                     Your Application                         │
│                                                              │
│  ┌──────────────────┐         ┌──────────────────┐         │
│  │  Concrete Class  │         │    Interface     │         │
│  │                  │         │   (Dubbo/Feign)  │         │
│  │  UserService {   │         │                  │         │
│  │    getUser() {   │         │  UserServiceApi {│         │
│  │      // impl     │         │    getUser();    │         │
│  │    }             │         │  }               │         │
│  │  }               │         │                  │         │
│  └────────┬─────────┘         └────────┬─────────┘         │
│           │                            │                    │
└───────────┼────────────────────────────┼────────────────────┘
            │                            │
            │ Method Call                │ Method Call
            ▼                            ▼
┌───────────────────────────────────────────────────────────────┐
│                    ByteBuddy Agent                            │
│                                                               │
│  ┌─────────────────────────────────────────────────────────┐ │
│  │              Type Detection                             │ │
│  │                                                         │ │
│  │  if (typeDescription.isInterface()) {                  │ │
│  │      // Use InterfaceInterceptor                       │ │
│  │  } else {                                              │ │
│  │      // Use Regular Interceptor                        │ │
│  │  }                                                     │ │
│  └─────────────────────────────────────────────────────────┘ │
│           │                            │                      │
│           ▼                            ▼                      │
│  ┌──────────────────┐       ┌──────────────────┐            │
│  │    Interceptor   │       │ InterfaceInter-  │            │
│  │                  │       │    ceptor        │            │
│  │ + @SuperCall     │       │ (no SuperCall)   │            │
│  │ + Can fallback   │       │ Must return mock │            │
│  └────────┬─────────┘       └────────┬─────────┘            │
│           │                          │                       │
│           └──────────┬───────────────┘                       │
│                      ▼                                       │
│           ┌─────────────────────┐                           │
│           │  Check Mock Config  │                           │
│           └──────────┬──────────┘                           │
│                      │                                       │
│         ┌────────────┴────────────┐                         │
│         ▼                         ▼                         │
│  ┌─────────────┐          ┌─────────────┐                  │
│  │ Mock Found  │          │ No Mock     │                  │
│  └──────┬──────┘          └──────┬──────┘                  │
│         │                        │                          │
│         ▼                        ▼                          │
│  ┌─────────────┐          ┌─────────────┐                  │
│  │ Return Mode │          │ Interface?  │                  │
│  │ Exception?  │          └──────┬──────┘                  │
│  └──────┬──────┘                 │                          │
│         │              ┌─────────┴─────────┐                │
│    ┌────┴────┐         ▼                   ▼                │
│    ▼         ▼    Call Original    Throw Exception         │
│  Return   Throw   (SuperCall)      (No mock for            │
│  Mock     Exception                 interface)              │
│  Value                                                      │
└─────────────────────────────────────────────────────────────┘
            │                            │
            ▼                            ▼
┌───────────────────────────────────────────────────────────────┐
│                    Return to Application                      │
│                                                               │
│  • Mocked value (if configured)                              │
│  • Exception (if exception mode)                             │
│  • Original result (if no mock for concrete class)           │
│  • UnsupportedOperationException (if no mock for interface)  │
└───────────────────────────────────────────────────────────────┘
```

## Key Components

### 1. MockAgent
- Entry point: `premain()` method
- Loads mock configuration from JSON file
- Installs ByteBuddy agent with transformers

### 2. Type Detection
```java
boolean isInterface = typeDescription.isInterface();
```
- Determines if target is interface or concrete class
- Routes to appropriate interceptor

### 3. Regular Interceptor (Concrete Classes)
```java
@RuntimeType
public static Object intercept(
    @Origin Method method,
    @AllArguments Object[] args,
    @SuperCall Callable<?> zuper  // ← Can call original
) {
    if (mockConfigured) {
        return mockValue;
    } else {
        return zuper.call();  // ← Fallback to original
    }
}
```

### 4. InterfaceInterceptor (Interfaces)
```java
@RuntimeType
public static Object intercept(
    @Origin Method method,
    @AllArguments Object[] args
    // No @SuperCall - no implementation to call!
) {
    if (mockConfigured) {
        return mockValue;
    } else {
        throw new UnsupportedOperationException();
    }
}
```

## Data Flow

### Scenario 1: Concrete Class with Mock
```
UserService.getUser()
  → Regular Interceptor
  → Check config: Mock found
  → Return mock value
  → Application receives mocked User
```

### Scenario 2: Concrete Class without Mock
```
UserService.getUser()
  → Regular Interceptor
  → Check config: No mock
  → Call zuper.call()
  → Execute original implementation
  → Application receives real User
```

### Scenario 3: Interface with Mock (Dubbo/Feign)
```
UserServiceApi.getUser()
  → InterfaceInterceptor
  → Check config: Mock found
  → Return mock value
  → Application receives mocked User
```

### Scenario 4: Interface without Mock
```
UserServiceApi.getUser()
  → InterfaceInterceptor
  → Check config: No mock
  → Throw UnsupportedOperationException
  → Application sees error (configure mock!)
```

## Configuration Storage

```
.idea/mockRunnerConfig.xml
  ↓
MockConfig {
  mockRules: Map<String, MockRule>
  mockMethods: List<MockMethodConfig>
}
  ↓
Serialized to JSON at runtime
  ↓
Loaded by MockAgent.premain()
```

## UI Components

```
Mock Runner Tool Window
  ↓
MockRunnerToolWindowContent
  ↓
JTable with custom model
  ↓
JsonTableCellEditor (for editing values)
  ↓
InlineJsonEditor (syntax highlighting)
  ↓
Saves to MockConfigService
  ↓
Persists to .idea/mockRunnerConfig.xml
```

## Build Process

```
Source Code
  ↓
javac (compile)
  ↓
jar (package with dependencies)
  ↓
mock-agent-1.0.6-agent.jar
  ↓
Loaded via -javaagent flag
  ↓
Injected by MockRunConfigurationExtension
```

## Runtime Injection

```
IntelliJ Run/Debug
  ↓
MockRunConfigurationExtension.updateJavaParameters()
  ↓
Add VM option: -javaagent:/path/to/mock-agent.jar=/path/to/config.json
  ↓
JVM starts with agent
  ↓
Agent.premain() called before main()
  ↓
ByteBuddy transforms classes
  ↓
Application runs with mocked methods
```

## Technology Stack

- **ByteBuddy**: Java agent and bytecode manipulation
- **Gson**: JSON parsing for mock values and configuration
- **IntelliJ Platform SDK**: UI components and IDE integration
- **Java Reflection**: Type information and method metadata
- **Dynamic Proxy**: Used by Dubbo/Feign (intercepted by our agent)

## Performance Considerations

- Agent only intercepts configured methods (not all methods)
- Type detection happens once during class loading
- Mock lookup is O(1) using HashMap
- JSON parsing only happens when mock is returned
- No performance impact on non-mocked methods

## Security Considerations

- Agent only loads in development environment
- Configuration file is local to project
- No network calls or external dependencies at runtime
- Mock values are validated before parsing
- Exception types are validated before instantiation
