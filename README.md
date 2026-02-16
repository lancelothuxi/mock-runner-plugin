# Mock Runner - IntelliJ IDEA Plugin

[![Version](https://img.shields.io/badge/version-1.0.5-blue.svg)](https://github.com/lancelothuxi/mock-runner-plugin)
[![License](https://img.shields.io/badge/license-MIT-green.svg)](LICENSE)

A powerful IntelliJ IDEA plugin that enables runtime method mocking using Java Agent technology. Mock any method's return value or exception without modifying your source code.

## Features

- 🎯 **Runtime Method Mocking** - Mock method return values at runtime using Java Agent
- 🔄 **No Code Changes** - Mock methods without modifying source code
- 📝 **Visual Configuration** - Easy-to-use UI for managing mock configurations
- 🎨 **JSON Editor** - Built-in JSON editor with syntax highlighting for complex return values
- ⚡ **Exception Mocking** - Configure methods to throw exceptions instead of returning values
- 🔍 **Method Detection** - Automatic detection of mockable methods with gutter icons
- 💾 **Persistent Configuration** - Mock configurations are saved per project
- 🎛️ **Enable/Disable Toggle** - Quickly enable or disable individual mocks
- 🔎 **Search & Filter** - Search through mock configurations
- 📄 **Pagination** - Handle large numbers of mock configurations efficiently

## Installation

### From IntelliJ Marketplace (Recommended)

1. Open IntelliJ IDEA
2. Go to `Settings/Preferences` → `Plugins`
3. Search for "Mock Runner"
4. Click `Install`
5. Restart IDE

### Manual Installation

1. Download the latest release from [GitHub Releases](https://github.com/lancelothuxi/mock-runner-plugin/releases)
2. Open IntelliJ IDEA
3. Go to `Settings/Preferences` → `Plugins` → `⚙️` → `Install Plugin from Disk...`
4. Select the downloaded `.zip` file
5. Restart IDE

## Quick Start

### 1. Add Mock Configuration

**Method 1: Right-click on Method**
1. Open a Java file
2. Right-click on any method
3. Select `Add Mock for Method`
4. Configure the mock return value or exception
5. Click `OK`

**Method 2: Use Gutter Icon**
1. Look for the ▶️ icon in the gutter next to methods
2. Click the icon to add mock configuration

### 2. Configure Mock Value

#### Return Value Mode
```json
{
  "name": "John Doe",
  "age": 25,
  "email": "john@example.com"
}
```

For collections:
```json
[
  {"id": 1, "name": "Alice"},
  {"id": 2, "name": "Bob"}
]
```

#### Exception Mode
1. Check "Throw exception instead of returning value"
2. Enter exception class: `java.lang.RuntimeException`
3. Enter exception message: `Mocked exception for testing`

### 3. Run with Mock

1. Open the `Mock Runner` tool window (right sidebar)
2. Verify your mock configurations
3. Right-click on your main class or test
4. Select `Run with Mock` or `Debug with Mock`
5. Your application runs with mocked methods!

## Usage Examples

### Example 1: Mock Database Service

```java
public class UserService {
    public User getUserById(String id) {
        // Real database call
        return database.findUser(id);
    }
}
```

**Mock Configuration:**
- Method: `UserService.getUserById`
- Return Value:
```json
{
  "id": "123",
  "name": "Test User",
  "email": "test@example.com",
  "role": "ADMIN"
}
```

### Example 2: Mock List Return

```java
public class StudentService {
    public List<Student> getAllStudents() {
        return repository.findAll();
    }
}
```

**Mock Configuration:**
- Method: `StudentService.getAllStudents`
- Return Type: `List<com.example.Student>`
- Return Value:
```json
[
  {"id": "001", "name": "Alice", "grade": "A"},
  {"id": "002", "name": "Bob", "grade": "B"}
]
```

### Example 3: Mock Exception

```java
public class PaymentService {
    public Payment processPayment(String orderId) {
        return paymentGateway.charge(orderId);
    }
}
```

**Mock Configuration:**
- Method: `PaymentService.processPayment`
- Mode: Exception
- Exception Type: `java.lang.IllegalStateException`
- Message: `Payment gateway unavailable`

## Mock Runner Tool Window

The Mock Runner tool window provides a centralized view of all mock configurations:

### Features

- **Search**: Filter mocks by class or method name
- **Enable/Disable**: Toggle individual mocks on/off
- **Edit**: Click on values to edit inline
- **Delete**: Remove mock configurations
- **Mode Switch**: Toggle between Return Value and Exception modes
- **Pagination**: Navigate through large lists (20 items per page)
- **Global Toggle**: Enable/disable all mocks at once
- **Clear All**: Remove all mock configurations (with confirmation)

### Table Columns

| Column | Description |
|--------|-------------|
| Enabled | Checkbox to enable/disable the mock |
| Class | The class containing the method |
| Method | The method name |
| Args | Method signature/parameters |
| Mode | Return Value or Exception |
| Value | The mock return value or exception details |

## Advanced Features

### Generic Type Support

The plugin automatically handles generic types correctly:

```java
// Automatically parsed as List<Student>, not List<LinkedTreeMap>
List<Student> students = service.getAllStudents();

// Works with nested generics
Map<String, List<User>> userGroups = service.getUserGroups();
```

### Exception Handling

Configure methods to throw exceptions for testing error scenarios:

1. Select "Exception" mode in the table
2. Click on the Value cell
3. Enter exception type and message
4. The method will throw the specified exception at runtime

### Inline JSON Editing

The plugin provides a professional JSON editor with:
- Syntax highlighting
- Auto-formatting
- Validation
- Large text support

## Configuration Files

Mock configurations are stored in:
```
.idea/mockRunnerConfig.xml
```

You can commit this file to version control to share mock configurations with your team.

## Troubleshooting

### Mock not working?

1. **Check if mock is enabled**: Look in the Mock Runner tool window
2. **Verify method signature**: Ensure the method signature matches exactly
3. **Check return type**: Verify the return type is correctly specified
4. **Review logs**: Check IDE logs for any errors

### ClassCastException with generics?

The plugin uses Java reflection to get the actual generic type at runtime. If you encounter issues:
1. Ensure you're using version 1.0.5 or later
2. Check that the return type in the configuration matches the method signature

### Agent not loading?

1. Verify the plugin is installed correctly
2. Try restarting the IDE
3. Check that you're using "Run with Mock" not regular "Run"

## Requirements

- IntelliJ IDEA 2022.3 or later
- Java 11 or later
- Gradle/Maven project (for Java applications)

## Technology Stack

- **Java Agent**: ByteBuddy for runtime method interception
- **JSON Processing**: Gson for parsing mock values
- **UI Framework**: IntelliJ Platform SDK
- **Build Tool**: Gradle

## Contributing

Contributions are welcome! Please feel free to submit a Pull Request.

1. Fork the repository
2. Create your feature branch (`git checkout -b feature/AmazingFeature`)
3. Commit your changes (`git commit -m 'Add some AmazingFeature'`)
4. Push to the branch (`git push origin feature/AmazingFeature`)
5. Open a Pull Request

## License

This project is licensed under the MIT License - see the [LICENSE](LICENSE) file for details.

## Support

- 🐛 [Report Issues](https://github.com/lancelothuxi/mock-runner-plugin/issues)
- 💬 [Discussions](https://github.com/lancelothuxi/mock-runner-plugin/discussions)
- 📧 Email: lancelothuxi@gmail.com

## Changelog

### Version 1.0.5 (2026-02-16)
- ✨ Added exception mocking support
- 🎨 Improved JSON editor with inline editing
- 🔧 Fixed generic type parsing (List<T>, Map<K,V>)
- 🚀 Performance improvements
- 📦 Package refactoring to io.github.lancelothuxi

### Version 1.0.4
- 🎯 Added pagination support
- 🔍 Added search functionality
- ⚡ Enhanced table UI with enable/disable toggles

### Version 1.0.0
- 🎉 Initial release
- ✨ Basic mock functionality
- 📝 Visual configuration UI

## Acknowledgments

- Built with [IntelliJ Platform SDK](https://plugins.jetbrains.com/docs/intellij/)
- Uses [ByteBuddy](https://bytebuddy.net/) for Java Agent
- JSON editing powered by [Gson](https://github.com/google/gson)

---

Made with ❤️ by [Lancelot Huxi](https://github.com/lancelothuxi)
