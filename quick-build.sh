#!/bin/bash
# 快速构建脚本 - 只构建必要的部分

echo "=== 快速构建 Mock Runner Plugin ==="

# 1. 只编译 Java 代码（最快）
echo "1. 编译 Java 代码..."
./gradlew compileJava --parallel --build-cache

# 2. 构建 agent jar
echo "2. 构建 agent jar..."
./gradlew agentJar --parallel --build-cache

# 3. 打包插件（不运行测试）
echo "3. 打包插件..."
./gradlew prepareSandbox --parallel --build-cache

echo ""
echo "✅ 构建完成！"
echo ""
echo "现在你可以："
echo "  - 在 IntelliJ 中重新运行插件测试实例"
echo "  - 或运行: ./gradlew runIde"
