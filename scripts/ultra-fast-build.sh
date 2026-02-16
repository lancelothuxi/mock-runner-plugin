#!/bin/bash
# 超快速构建 - 完全跳过 Gradle

set -e

echo "=== 超快速构建（完全跳过 Gradle）==="

# 1. 编译 agent
echo "1. 编译 agent..."
BYTEBUDDY=$(find ~/.gradle/caches -name "byte-buddy-1.*.jar" | grep -v "agent" | head -1)
GSON=$(find ~/.gradle/caches -name "gson-*.jar" | head -1)

# 清理旧的编译文件
rm -rf build/fast-compile
mkdir -p build/fast-compile

javac -d build/fast-compile \
    -cp "$GSON:$BYTEBUDDY" \
    src/main/java/io/github/lancelothuxi/idea/plugin/mock/mock/*.java \
    src/main/java/io/github/lancelothuxi/idea/plugin/mock/agent/MockAgent.java

# 2. 解压依赖到临时目录
echo "2. 准备依赖..."
cd build/fast-compile
jar xf "$BYTEBUDDY"
jar xf "$GSON"
rm -rf META-INF/maven META-INF/*.SF META-INF/*.RSA META-INF/*.DSA
cd ../..

# 3. 创建 MANIFEST
mkdir -p build/fast-compile/META-INF
cat > build/fast-compile/META-INF/MANIFEST.MF << 'EOF'
Manifest-Version: 1.0
Premain-Class: io.github.lancelothuxi.idea.plugin.mock.agent.MockAgent
Can-Redefine-Classes: true
Can-Retransform-Classes: true

EOF

# 4. 打包
echo "3. 打包 agent jar..."
AGENT_JAR="build/idea-sandbox/plugins/my-idea-plugin/lib/mock-agent-1.0.5-agent.jar"
mkdir -p "$(dirname "$AGENT_JAR")"

cd build/fast-compile
jar cfm "../../$AGENT_JAR" META-INF/MANIFEST.MF .
cd ../..

echo ""
echo "✅ 完成！用时不到 3 秒"
echo "   Agent jar: $AGENT_JAR"
echo ""
echo "现在在 IntelliJ 中重新运行你的应用！"
