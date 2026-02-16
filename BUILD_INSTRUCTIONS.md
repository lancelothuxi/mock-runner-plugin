# Build Instructions

## Prerequisites

- Java 17 or higher
- Gradle 8.5 (included via wrapper)

## Quick Start

### Option 1: Use the Java 17 Wrapper (Recommended)

```bash
# Stop any existing Gradle daemons
./gradlew-java17.sh --stop

# Clean build
./gradlew-java17.sh clean build

# Run tests
./gradlew-java17.sh test

# Build plugin distribution
./gradlew-java17.sh buildPlugin
```

### Option 2: Set JAVA_HOME Manually

```bash
export JAVA_HOME=/usr/lib/jvm/java-17-openjdk-amd64
./gradlew --stop
./gradlew clean build
```

### Option 3: Use Maven

```bash
./mvnw clean package
```

## Common Issues

### "compatible with Java 8" Error

**Problem**: Gradle is using Java 8, but the IntelliJ plugin requires Java 11+

**Solution**:
```bash
./gradlew-java17.sh --stop  # Stop Java 8 daemons
./gradlew-java17.sh clean   # Build with Java 17
```

### SSL/Network Errors

**Problem**: Cannot download dependencies due to firewall/SSL issues

**Solutions**:
1. Use a VPN
2. Configure proxy in `gradle.properties`
3. Use Maven instead: `./mvnw clean package`

## Build Outputs

- Plugin ZIP: `build/distributions/my-idea-plugin-1.0.6.zip`
- Agent JAR: `build/idea-sandbox/plugins/my-idea-plugin/lib/mock-agent-1.0.6-agent.jar`
- Test Reports: `build/reports/tests/test/index.html`

## Development Workflow

### 1. Make Code Changes

Edit files in `src/main/java/`

### 2. Quick Agent Build (Fast)

```bash
./scripts/ultra-fast-build.sh
```

This compiles only the agent (< 3 seconds).

### 3. Full Build (Slow)

```bash
./gradlew-java17.sh clean build
```

This builds everything including plugin packaging.

### 4. Run Tests

```bash
# All tests
./gradlew-java17.sh test

# Specific test
./gradlew-java17.sh test --tests "test.dubbo.DubboServiceTest"
```

### 5. Run Plugin in Sandbox

```bash
./gradlew-java17.sh runIde
```

This launches IntelliJ with your plugin installed.

## CI/CD

For automated builds, ensure Java 17 is available:

```yaml
# GitHub Actions example
- uses: actions/setup-java@v2
  with:
    java-version: '17'
    
- name: Build
  run: ./gradlew clean build
```

## Troubleshooting

See [GRADLE_SETUP.md](GRADLE_SETUP.md) for detailed troubleshooting guide.

### Quick Checks

```bash
# Check Java version
java -version  # Should show 17.x

# Check Gradle Java version
./gradlew-java17.sh --version  # Should show JVM 17.x

# Check Gradle daemon status
./gradlew-java17.sh --status

# Force refresh dependencies
./gradlew-java17.sh clean --refresh-dependencies
```

## Project Structure

```
.
├── src/
│   ├── main/java/          # Plugin source code
│   ├── main/resources/     # Plugin resources
│   ├── test/java/test/     # Test source code
│   └── test/resources/     # Test resources
├── build.gradle.kts        # Gradle build configuration
├── settings.gradle.kts     # Gradle settings
├── gradle.properties       # Gradle properties (Java 17 home)
├── gradlew-java17.sh       # Wrapper script for Java 17
└── scripts/                # Build scripts
```

## Additional Resources

- [TestNG Test Guide](docs/TESTNG_GUIDE.md)
- [Dubbo/Feign Guide](docs/DUBBO_FEIGN_GUIDE.md)
- [Architecture Diagram](docs/ARCHITECTURE_DIAGRAM.md)
- [Gradle Setup](GRADLE_SETUP.md)
