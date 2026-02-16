#!/bin/bash
# Quick test script with agent auto-attachment

set -e

echo "╔════════════════════════════════════════════════════════════╗"
echo "║         Mock Runner - Test with Agent                     ║"
echo "╚════════════════════════════════════════════════════════════╝"
echo ""

# Force Java 17
export JAVA_HOME=/usr/lib/jvm/java-17-openjdk-amd64
export PATH=$JAVA_HOME/bin:$PATH

echo "Step 1: Building agent JAR..."
./gradlew agentJar

echo ""
echo "Step 2: Running tests with agent..."
./gradlew test

echo ""
echo "╔════════════════════════════════════════════════════════════╗"
echo "║                    Tests Complete!                         ║"
echo "╚════════════════════════════════════════════════════════════╝"
echo ""
echo "Test reports: build/reports/tests/test/index.html"
