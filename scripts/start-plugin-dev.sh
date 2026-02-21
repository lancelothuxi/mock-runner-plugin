#!/bin/bash
# 插件开发启动脚本 - 自动处理 agent 更新

set -e

echo "=== Mock Runner Plugin 开发环境启动 ==="
echo ""

# 1. 快速编译最新的 agent
echo "1. 编译最新的 agent jar..."
bash scripts/ultra-fast-build.sh

echo ""
echo "2. 启动 IntelliJ sandbox..."
echo "   注意：sandbox 启动后，agent jar 会被 Gradle 覆盖"
echo "   我们会在启动后立即重新编译"
echo ""

# 3. 在后台启动 runIde，并监控日志
echo "启动中，请稍候..."
./gradlew runIde > runide.log 2>&1 &
GRADLE_PID=$!

echo "Gradle PID: $GRADLE_PID"
echo ""

# 4. 等待 sandbox 启动（监控日志）
echo "等待 sandbox 启动..."
timeout=300  # 5分钟超时
elapsed=0

while [ $elapsed -lt $timeout ]; do
    if grep -q "IDE STARTED" runide.log 2>/dev/null; then
        echo "✓ Sandbox 已启动！"
        break
    fi
    
    if grep -q "BUILD FAILED" runide.log 2>/dev/null; then
        echo "❌ 构建失败，查看 runide.log"
        exit 1
    fi
    
    # 显示进度
    if [ $((elapsed % 10)) -eq 0 ]; then
        echo "  等待中... ${elapsed}s"
    fi
    
    sleep 2
    elapsed=$((elapsed + 2))
done

if [ $elapsed -ge $timeout ]; then
    echo "❌ 启动超时"
    exit 1
fi

# 5. Sandbox 启动后，立即重新编译 agent（覆盖 Gradle 的版本）
echo ""
echo "3. 重新编译 agent（覆盖 Gradle 版本）..."
sleep 3  # 等待文件系统稳定
bash scripts/ultra-fast-build.sh

echo ""
echo "=========================================="
echo "✅ 开发环境已就绪！"
echo "=========================================="
echo ""
echo "现在你可以："
echo "  1. 在 sandbox IntelliJ 中打开你的测试项目"
echo "  2. 使用 'Run with Mock Runner' 运行应用"
echo "  3. 查看控制台日志"
echo ""
echo "修改 agent 代码后，运行："
echo "  ./ultra-fast-build.sh"
echo "  然后在 sandbox 中重新运行应用"
echo ""
echo "查看日志："
echo "  tail -f runide.log"
echo ""
