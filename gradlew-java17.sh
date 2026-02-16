#!/bin/bash
# Gradle wrapper that forces Java 17

export JAVA_HOME=/usr/lib/jvm/java-17-openjdk-amd64
export PATH=$JAVA_HOME/bin:$PATH

echo "Using Java: $(java -version 2>&1 | head -1)"
echo "JAVA_HOME: $JAVA_HOME"
echo ""

./gradlew "$@"
