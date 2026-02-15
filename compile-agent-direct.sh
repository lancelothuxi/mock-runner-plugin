#!/bin/bash
# 直接编译 agent - 跳过 Gradle，超快！

echo "=== 直接编译 MockAgent（跳过 Gradle）==="

# 设置路径
AGENT_SRC="src/main/java/com/example/plugin/agent/MockAgent.java"
MOCK_SRC="src/main/java/com/example/plugin/mock"
BUILD_DIR="build/agent-classes"
AGENT_JAR="build/idea-sandbox/plugins/my-idea-plugin/lib/mock-agent-1.0.0-agent.jar"

# 创建输出目录
mkdir -p "$BUILD_DIR/com/example/plugin/agent"
mkdir -p "$BUILD_DIR/com/example/plugin/mock"

# 查找依赖 jar
BYTEBUDDY_JAR=$(find ~/.gradle/caches -name "byte-buddy-*.jar" | grep -v "agent" | head -1)
GSON_JAR=$(find ~/.gradle/caches -name "gson-*.jar" | head -1)

if [ -z "$BYTEBUDDY_JAR" ] || [ -z "$GSON_JAR" ]; then
    echo "❌ 找不到依赖 jar，运行一次 Gradle 构建来下载依赖"
    exit 1
fi

echo "✓ 找到 ByteBuddy: $BYTEBUDDY_JAR"
echo "✓ 找到 Gson: $GSON_JAR"

# 编译 Mock 配置类
echo "1. 编译 Mock 配置类..."
javac -d "$BUILD_DIR" \
    -cp "$GSON_JAR" \
    "$MOCK_SRC/MockConfig.java" \
    "$MOCK_SRC/MockMethodConfig.java"

# 编译 Agent
echo "2. 编译 MockAgent..."
javac -d "$BUILD_DIR" \
    -cp "$BYTEBUDDY_JAR:$GSON_JAR:$BUILD_DIR" \
    "$AGENT_SRC"

# 创建 MANIFEST
echo "3. 创建 MANIFEST..."
mkdir -p "$BUILD_DIR/META-INF"
cat > "$BUILD_DIR/META-INF/MANIFEST.MF" << 'EOF'
Manifest-Version: 1.0
Premain-Class: com.example.plugin.agent.MockAgent
Can-Redefine-Classes: true
Can-Retransform-Classes: true

EOF

# 打包 jar（包含依赖）
echo "4. 打包 agent jar..."
cd "$BUILD_DIR"
jar xf "$BYTEBUDDY_JAR"
jar xf "$GSON_JAR"
rm -rf META-INF/maven META-INF/*.SF META-INF/*.RSA META-INF/*.DSA

# 重新创建 MANIFEST
cat > "META-INF/MANIFEST.MF" << 'EOF'
Manifest-Version: 1.0
Premain-Class: com.example.plugin.agent.MockAgent
Can-Redefine-Classes: true
Can-Retransform-Classes: true

EOF

cd - > /dev/null

# 创建最终 jar
rm -f "$AGENT_JAR"
cd "$BUILD_DIR"
jar cfm "../../../$AGENT_JAR" "META-INF/MANIFEST.MF" .
cd - > /dev/null

echo ""
echo "✅ 编译完成！Agent jar 已更新"
echo "   位置: $AGENT_JAR"
echo ""
echo "现在重新运行你的应用即可！"
