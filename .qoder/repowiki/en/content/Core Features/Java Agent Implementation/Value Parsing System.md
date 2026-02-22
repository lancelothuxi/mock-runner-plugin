# Value Parsing System

<cite>
**Referenced Files in This Document**
- [MockAgent.java](file://src/main/java/io/github/lancelothuxi/idea/plugin/mock/agent/MockAgent.java)
- [MockValueGenerator.java](file://src/main/java/io/github/lancelothuxi/idea/plugin/mock/util/MockValueGenerator.java)
- [MockConfig.java](file://src/main/java/io/github/lancelothuxi/idea/plugin/mock/mock/MockConfig.java)
- [MockConfigService.java](file://src/main/java/io/github/lancelothuxi/idea/plugin/mock/service/MockConfigService.java)
- [AddMockDialog.java](file://src/main/java/io/github/lancelothuxi/idea/plugin/mock/ui/AddMockDialog.java)
- [ExceptionMockingTest.java](file://src/test/java/test/ExceptionMockingTest.java)
- [User.java](file://src/test/java/test/User.java)
- [Student.java](file://src/test/java/test/Student.java)
</cite>

## Table of Contents
1. [Introduction](#introduction)
2. [System Architecture](#system-architecture)
3. [Core Components](#core-components)
4. [Architecture Overview](#architecture-overview)
5. [Detailed Component Analysis](#detailed-component-analysis)
6. [Dependency Analysis](#dependency-analysis)
7. [Performance Considerations](#performance-considerations)
8. [Troubleshooting Guide](#troubleshooting-guide)
9. [Conclusion](#conclusion)

## Introduction

The mock value parsing system is a sophisticated component within the Mock Runner plugin that handles the conversion of mock configuration values into appropriate Java objects at runtime. This system bridges the gap between user-friendly JSON configuration and strongly-typed Java method return values, supporting primitive types, complex objects, collections, and generic types.

The system operates through a multi-layered approach that combines primitive type conversion, JSON deserialization using Gson, and intelligent type resolution for complex generics. It provides robust error handling, fallback mechanisms, and comprehensive support for custom exception creation.

## System Architecture

The value parsing system follows a layered architecture with clear separation of concerns:

```mermaid
graph TB
subgraph "Configuration Layer"
Config[MockConfig]
ConfigService[MockConfigService]
Dialog[AddMockDialog]
end
subgraph "Parsing Engine"
Agent[MockAgent]
Interceptor[Interceptor.parseMockValue]
Gson[Gson Parser]
end
subgraph "Type Resolution"
Primitive[Primitive Types]
Generic[Generic Types]
Collections[Collections & Maps]
Objects[Custom Objects]
end
subgraph "Error Handling"
Exceptions[Custom Exceptions]
Fallback[Fallback Mechanisms]
Logging[Logging System]
end
Config --> ConfigService
ConfigService --> Agent
Dialog --> ConfigService
Agent --> Interceptor
Interceptor --> Gson
Gson --> Primitive
Gson --> Generic
Gson --> Collections
Gson --> Objects
Interceptor --> Exceptions
Interceptor --> Fallback
Interceptor --> Logging
```

**Diagram sources**
- [MockAgent.java](file://src/main/java/io/github/lancelothuxi/idea/plugin/mock/agent/MockAgent.java#L246-L326)
- [MockConfig.java](file://src/main/java/io/github/lancelothuxi/idea/plugin/mock/mock/MockConfig.java#L1-L218)
- [MockConfigService.java](file://src/main/java/io/github/lancelothuxi/idea/plugin/mock/service/MockConfigService.java#L1-L197)

## Core Components

### MockAgent.Interceptor.parseMockValue

The central parsing mechanism that handles all value conversions. This method implements a comprehensive type resolution strategy:

```mermaid
flowchart TD
Start([parseMockValue Entry]) --> NullCheck{Value is null?}
NullCheck --> |Yes| ReturnNull[Return null]
NullCheck --> |No| TypeSwitch[Primitive Type Check]
TypeSwitch --> IntCheck{Is integer type?}
IntCheck --> |Yes| ParseInt[Parse integer]
IntCheck --> |No| LongCheck{Is long type?}
LongCheck --> |Yes| ParseLong[Parse long]
LongCheck --> |No| DoubleCheck{Is double type?}
DoubleCheck --> |Yes| ParseDouble[Parse double]
DoubleCheck --> |No| FloatCheck{Is float type?}
FloatCheck --> |Yes| ParseFloat[Parse float]
FloatCheck --> |No| BoolCheck{Is boolean type?}
BoolCheck --> |Yes| ParseBool[Parse boolean]
BoolCheck --> |No| StringCheck{Is string type?}
StringCheck --> |Yes| ReturnString[Return string]
StringCheck --> |No| JsonCheck{Is JSON value?}
JsonCheck --> |Yes| GenericCheck{Contains generic types?}
GenericCheck --> |Yes| ListCheck{Is List type?}
ListCheck --> |Yes| ParseList[Parse with TypeToken]
ListCheck --> |No| ClassCheck{Can resolve class?}
ClassCheck --> |Yes| ParseClass[Parse with class type]
ClassCheck --> |No| FallbackGeneric[Fallback to generic parsing]
JsonCheck --> |No| ReturnOriginal[Return original value]
ParseList --> Success[Return parsed result]
ParseClass --> Success
FallbackGeneric --> Success
ParseInt --> Success
ParseLong --> Success
ParseDouble --> Success
ParseFloat --> Success
ParseBool --> Success
ReturnString --> Success
ReturnNull --> End([Exit])
Success --> End
ReturnOriginal --> End
```

**Diagram sources**
- [MockAgent.java](file://src/main/java/io/github/lancelothuxi/idea/plugin/mock/agent/MockAgent.java#L246-L326)

### MockValueGenerator

The value generation component that creates mock data for UI and testing scenarios:

```mermaid
classDiagram
class MockValueGenerator {
-Gson GSON
-Set~String~ PROCESSING_TYPES
+generateMockValue(PsiType) String
-generateMockValueInternal(PsiType) String
-generateListMockValue(PsiClassType) String
-generateMapMockValue(PsiClassType) String
-generateObjectJson(PsiClass) String
-getDefaultValueObject(PsiType) Object
}
class MockValueGenerator {
+generateMockValue(PsiType) String
+generateListMockValue(PsiClassType) String
+generateMapMockValue(PsiClassType) String
+generateObjectJson(PsiClass) String
+getDefaultValueObject(PsiType) Object
}
MockValueGenerator --> Gson : "uses"
```

**Diagram sources**
- [MockValueGenerator.java](file://src/main/java/io/github/lancelothuxi/idea/plugin/mock/util/MockValueGenerator.java#L1-L289)

**Section sources**
- [MockAgent.java](file://src/main/java/io/github/lancelothuxi/idea/plugin/mock/agent/MockAgent.java#L246-L326)
- [MockValueGenerator.java](file://src/main/java/io/github/lancelothuxi/idea/plugin/mock/util/MockValueGenerator.java#L1-L289)

## Architecture Overview

The value parsing system integrates seamlessly with the broader Mock Runner architecture:

```mermaid
sequenceDiagram
participant UI as User Interface
participant Config as MockConfigService
participant Agent as MockAgent
participant Interceptor as Interceptor
participant Parser as parseMockValue
participant Gson as Gson Parser
participant Target as Target Method
UI->>Config : Configure mock value
Config->>Agent : Save configuration
Agent->>Agent : Load configuration
Target->>Interceptor : Method call intercepted
Interceptor->>Parser : parseMockValue(value, type)
Parser->>Parser : Primitive type check
Parser->>Parser : JSON format check
Parser->>Gson : Deserialize with type info
Gson-->>Parser : Parsed object
Parser-->>Interceptor : Return parsed value
Interceptor-->>Target : Return mock value
Note over UI,Target : Exception mode supported
Interceptor->>Interceptor : Check exception flag
Interceptor->>Interceptor : Create custom exception
```

**Diagram sources**
- [MockAgent.java](file://src/main/java/io/github/lancelothuxi/idea/plugin/mock/agent/MockAgent.java#L202-L244)
- [MockConfigService.java](file://src/main/java/io/github/lancelothuxi/idea/plugin/mock/service/MockConfigService.java#L41-L58)

## Detailed Component Analysis

### Primitive Type Conversion

The system provides comprehensive support for Java primitive types and their wrapper classes:

| Type | Supported Formats | Example |
|------|-------------------|---------|
| int | Integer.parseInt() | "42", "-17" |
| long | Long.parseLong() | "123456789L" |
| double | Double.parseDouble() | "3.14159" |
| float | Float.parseFloat() | "2.718f" |
| boolean | Boolean.parseBoolean() | "true", "false" |
| String | Direct assignment | "hello world" |

**Section sources**
- [MockAgent.java](file://src/main/java/io/github/lancelothuxi/idea/plugin/mock/agent/MockAgent.java#L254-L272)

### JSON Deserialization with Gson

The Gson integration provides powerful object parsing capabilities:

```mermaid
flowchart TD
JSONInput["JSON String Input"] --> TypeDetection{Type Detection}
TypeDetection --> ListType{List Type?}
TypeDetection --> MapType{Map Type?}
TypeDetection --> ObjectType{Object Type?}
ListType --> |Yes| GenericParsing[Generic Type Parsing]
MapType --> |Yes| MapParsing[Map Parsing]
ObjectType --> |Yes| ClassParsing[Class Parsing]
GenericParsing --> InnerTypeExtraction[Extract Inner Type]
InnerTypeExtraction --> ClassResolution[Resolve Inner Class]
ClassResolution --> TypeTokenCreation[Create TypeToken]
TypeTokenCreation --> GsonParse[Gson.fromJson]
MapParsing --> GsonParse
ClassParsing --> GsonParse
GsonParse --> Result[Deserialized Object]
```

**Diagram sources**
- [MockAgent.java](file://src/main/java/io/github/lancelothuxi/idea/plugin/mock/agent/MockAgent.java#L274-L318)

**Section sources**
- [MockAgent.java](file://src/main/java/io/github/lancelothuxi/idea/plugin/mock/agent/MockAgent.java#L274-L318)

### Generic Type Handling

The system excels at handling complex generic types:

#### List<T> Support
- **Pattern Recognition**: Detects `List<T>` and `java.util.List<T>` patterns
- **Inner Type Extraction**: Parses `<T>` portion to extract element type
- **TypeToken Creation**: Uses `TypeToken.getParameterized()` for proper type resolution
- **Fallback Mechanism**: Falls back to raw `List.class` if inner class not found

#### Map<K,V> Support  
- **Pattern Recognition**: Handles `Map<K,V>` and `java.util.Map<K,V>`
- **Parameter Extraction**: Extracts both key and value types
- **Type Safety**: Maintains type safety for both keys and values

**Section sources**
- [MockAgent.java](file://src/main/java/io/github/lancelothuxi/idea/plugin/mock/agent/MockAgent.java#L282-L295)

### Custom Object Parsing

The system supports parsing into custom classes with comprehensive field handling:

```mermaid
classDiagram
class CustomObject {
+String field1
+int field2
+String[] listField
+Map~String,Object~ mapField
}
class FieldProcessor {
+processBasicFields()
+processGetterMethods()
+handleNestedObjects()
+avoidCycles()
}
CustomObject --> FieldProcessor : "processed by"
```

**Diagram sources**
- [MockValueGenerator.java](file://src/main/java/io/github/lancelothuxi/idea/plugin/mock/util/MockValueGenerator.java#L138-L192)

**Section sources**
- [MockValueGenerator.java](file://src/main/java/io/github/lancelothuxi/idea/plugin/mock/util/MockValueGenerator.java#L138-L192)

### Exception Creation System

The system provides robust exception handling with custom exception types:

```mermaid
flowchart TD
ExceptionTrigger{Exception Mode} --> CheckType{Exception Type Valid?}
CheckType --> |Yes| ClassLoad[Load Exception Class]
CheckType --> |No| DefaultException[Use RuntimeException]
ClassLoad --> IsException{Is subclass of Exception?}
IsException --> |Yes| Constructor[Instantiate with message]
IsException --> |No| DefaultException
Constructor --> ThrowException[Throw Custom Exception]
DefaultException --> ThrowRuntimeException[Throw RuntimeException]
ThrowException --> End([Exception Thrown])
ThrowRuntimeException --> End
```

**Diagram sources**
- [MockAgent.java](file://src/main/java/io/github/lancelothuxi/idea/plugin/mock/agent/MockAgent.java#L328-L338)

**Section sources**
- [MockAgent.java](file://src/main/java/io/github/lancelothuxi/idea/plugin/mock/agent/MockAgent.java#L328-L338)
- [ExceptionMockingTest.java](file://src/test/java/test/ExceptionMockingTest.java#L38-L47)

## Dependency Analysis

The value parsing system has well-defined dependencies that ensure modularity and maintainability:

```mermaid
graph TB
subgraph "External Dependencies"
Gson[Gson Library]
ByteBuddy[ByteBuddy Agent Framework]
IntelliJ[IntelliJ Platform API]
end
subgraph "Internal Components"
MockAgent[MockAgent]
MockConfig[MockConfig]
MockValueGenerator[MockValueGenerator]
MockConfigService[MockConfigService]
AddMockDialog[AddMockDialog]
end
subgraph "Test Components"
ExceptionMockingTest[ExceptionMockingTest]
User[User Entity]
Student[Student Entity]
end
MockAgent --> Gson
MockAgent --> MockConfig
MockAgent --> ByteBuddy
MockConfigService --> MockConfig
MockConfigService --> Gson
MockConfigService --> IntelliJ
AddMockDialog --> MockConfigService
AddMockDialog --> MockValueGenerator
ExceptionMockingTest --> MockConfig
ExceptionMockingTest --> User
ExceptionMockingTest --> Student
```

**Diagram sources**
- [MockAgent.java](file://src/main/java/io/github/lancelothuxi/idea/plugin/mock/agent/MockAgent.java#L1-L20)
- [MockConfigService.java](file://src/main/java/io/github/lancelothuxi/idea/plugin/mock/service/MockConfigService.java#L1-L15)

**Section sources**
- [MockAgent.java](file://src/main/java/io/github/lancelothuxi/idea/plugin/mock/agent/MockAgent.java#L1-L20)
- [MockConfigService.java](file://src/main/java/io/github/lancelothuxi/idea/plugin/mock/service/MockConfigService.java#L1-L15)

## Performance Considerations

The value parsing system is designed with performance optimization in mind:

### Caching Strategies
- **Class Resolution Cache**: Prevents repeated Class.forName() calls
- **TypeToken Cache**: Reuses TypeToken instances for generic types
- **Gson Instance Management**: Creates Gson instances only when needed

### Lazy Evaluation
- **Conditional Parsing**: JSON parsing only occurs when value starts with `[` or `{`
- **Early Termination**: Primitive type checks short-circuit the parsing process
- **Minimal Object Creation**: Reuses existing objects where possible

### Memory Efficiency
- **Circular Reference Prevention**: Tracks processing types to prevent infinite recursion
- **Resource Cleanup**: Proper cleanup of temporary objects and collections

## Troubleshooting Guide

### Common Parsing Issues

#### Unknown Type Errors
**Symptoms**: Values not parsed correctly, returned as strings
**Causes**: 
- Unknown class names in generic types
- Incorrect type format in configuration
- Missing classpath entries

**Solutions**:
1. Verify class names in generic types (e.g., `List<com.student.Student>`)
2. Ensure classes are available in the classpath
3. Use fully qualified class names

#### JSON Parsing Failures
**Symptoms**: NullPointerException or ClassCastException
**Causes**:
- Malformed JSON syntax
- Type mismatch between JSON and expected type
- Missing fields in complex objects

**Solutions**:
1. Validate JSON syntax using online validators
2. Ensure JSON structure matches expected type
3. Check for missing required fields

#### Generic Type Resolution Issues
**Symptoms**: Runtime errors when parsing `List<T>` or `Map<K,V>`
**Causes**:
- Inner class not found
- Incorrect generic type syntax
- Missing type parameters

**Solutions**:
1. Verify inner class exists and is accessible
2. Use correct generic syntax: `List<com.example.User>`
3. Ensure all type parameters are specified

### Exception Handling Troubleshooting

#### Custom Exception Creation Failures
**Symptoms**: RuntimeException instead of expected exception type
**Causes**:
- Invalid exception class name
- Exception class not extending `java.lang.Exception`
- Missing constructor accepting String parameter

**Solutions**:
1. Verify exception class extends `java.lang.Exception`
2. Ensure constructor with String parameter exists
3. Use fully qualified class names

#### Configuration Switching Issues
**Symptoms**: Changes not taking effect immediately
**Causes**:
- Configuration not saved properly
- Agent not reloading configuration
- UI not refreshing state

**Solutions**:
1. Save configuration using the UI
2. Verify configuration file is written to temp directory
3. Restart IDE to force agent reload

**Section sources**
- [MockAgent.java](file://src/main/java/io/github/lancelothuxi/idea/plugin/mock/agent/MockAgent.java#L322-L326)
- [MockConfigService.java](file://src/main/java/io/github/lancelothuxi/idea/plugin/mock/service/MockConfigService.java#L41-L58)

## Conclusion

The mock value parsing system represents a sophisticated solution for converting user-friendly configuration into strongly-typed Java objects. Its multi-layered architecture provides comprehensive support for primitive types, complex objects, collections, and generic types, while maintaining robust error handling and performance characteristics.

The system's strength lies in its intelligent type resolution, particularly for generic types like `List<Student>`, and its flexible exception creation capabilities. The integration with Gson ensures reliable JSON deserialization while the careful error handling provides graceful fallback mechanisms.

Key benefits include:
- **Type Safety**: Maintains compile-time type checking
- **Flexibility**: Supports complex generic types and custom objects
- **Performance**: Optimized parsing with caching and lazy evaluation
- **Reliability**: Comprehensive error handling and fallback mechanisms
- **Extensibility**: Modular design allows for future enhancements

The system successfully bridges the gap between development convenience and production reliability, making mock configuration both powerful and safe to use in real-world applications.