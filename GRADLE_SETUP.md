# Gradle Build Setup Guide

## Java Version Issue

The IntelliJ Gradle plugin requires Java 11 or higher. This project is configured for Java 17.

### Problem

If you see this error:
```
No matching variant of org.jetbrains.intellij.plugins:gradle-intellij-plugin:1.17.4 was found.
The consumer was configured to find a library compatible with Java 8...
```

### Solution

1. **Stop all Gradle daemons** (they may be running with Java 8):
```bash
./gradlew --stop
```

2. **Verify Java 17 is being used**:
```bash
./gradlew --version
```

Should show:
```
JVM:          17.0.x
```

3. **If still using Java 8**, set JAVA_HOME explicitly:
```bash
export JAVA_HOME=/usr/lib/jvm/java-17-openjdk-amd64
./gradlew --stop
./gradlew clean build
```

4. **Make it permanent** (add to ~/.bashrc or ~/.zshrc):
```bash
export JAVA_HOME=/usr/lib/jvm/java-17-openjdk-amd64
export PATH=$JAVA_HOME/bin:$PATH
```

## SSL/Network Issues

If you encounter SSL handshake errors when downloading dependencies:

### Symptoms
```
Remote host terminated the handshake
Could not HEAD 'https://...'
```

### Solutions

1. **Use a VPN or proxy** if behind a firewall

2. **Download IntelliJ SDK manually**:
```bash
# Download from: https://www.jetbrains.com/intellij-repository/releases/
# Place in: ~/.gradle/caches/modules-2/files-2.1/com.jetbrains.intellij.idea/ideaIC/2022.3/
```

3. **Use local IntelliJ installation** (update build.gradle.kts):
```kotlin
intellij {
    localPath.set("/path/to/your/intellij")
}
```

4. **Try different mirror** (already configured in build.gradle.kts):
- Aliyun Maven mirror (for China)
- JetBrains cache redirector
- Gradle Plugin Portal

## Quick Build Commands

```bash
# Clean build (skip tests)
./gradlew clean build -x test

# Run tests only
./gradlew test

# Build plugin distribution
./gradlew buildPlugin

# Run plugin in sandbox IDE
./gradlew runIde
```

## Gradle Configuration Files

- `gradle.properties` - Sets Java 17 home and Gradle options
- `settings.gradle.kts` - Configures plugin repositories
- `build.gradle.kts` - Main build configuration

## Troubleshooting

### Gradle daemon issues
```bash
# Check daemon status
./gradlew --status

# Stop all daemons
./gradlew --stop

# Force refresh dependencies
./gradlew clean --refresh-dependencies
```

### Cache issues
```bash
# Clear Gradle cache
rm -rf ~/.gradle/caches/

# Clear project build
./gradlew clean
```

### Verify configuration
```bash
# Show project properties
./gradlew properties | grep java

# Show dependencies
./gradlew dependencies
```

## Recommended Setup

1. Install Java 17:
```bash
sudo apt install openjdk-17-jdk  # Ubuntu/Debian
```

2. Set JAVA_HOME permanently:
```bash
echo 'export JAVA_HOME=/usr/lib/jvm/java-17-openjdk-amd64' >> ~/.bashrc
echo 'export PATH=$JAVA_HOME/bin:$PATH' >> ~/.bashrc
source ~/.bashrc
```

3. Verify:
```bash
java -version  # Should show 17.x
./gradlew --version  # Should show JVM 17.x
```

4. Build:
```bash
./gradlew clean build
```

## Alternative: Use Maven

If Gradle continues to have issues, you can use Maven instead:

```bash
./mvnw clean package
```

The project includes both Gradle and Maven configurations.
