#!/bin/bash
# 直接测试 agent - 不需要 sandbox

echo "=== 直接测试 Mock Agent ==="
echo ""

# 1. 确保 agent jar 存在
AGENT_JAR="build/idea-sandbox/plugins/my-idea-plugin/lib/mock-agent-1.0.0-agent.jar"
if [ ! -f "$AGENT_JAR" ]; then
    echo "❌ Agent jar 不存在，先运行: ./ultra-fast-build.sh"
    exit 1
fi

echo "✓ Agent jar: $AGENT_JAR"
echo ""

# 2. 创建测试配置
cat > /tmp/mock-runner/mock-config.json << 'EOF'
{
  "mockRules": {
    "com.student.StudentService.getAllStudents": {
      "enabled": true,
      "returnValue": "[]",
      "returnType": "java.util.List"
    }
  },
  "mockMethods": []
}
EOF

echo "✓ Mock 配置已创建: /tmp/mock-runner/mock-config.json"
echo ""

# 3. 运行应用
echo "运行应用..."
echo "=========================================="
echo ""

cd ~/student-management
java -javaagent:"$AGENT_JAR"=/tmp/mock-runner/mock-config.json \
     -cp target/classes \
     com.student.StudentManagementApp

echo ""
echo "=========================================="
