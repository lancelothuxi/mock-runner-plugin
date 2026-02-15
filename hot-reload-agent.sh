#!/bin/bash
# 热更新 agent - 在 sandbox 运行时使用

echo "=== 热更新 Agent ==="

# 1. 快速编译
echo "1. 编译 agent..."
./ultra-fast-build.sh

# 2. 检查 sandbox 是否在运行
if pgrep -f "idea-sandbox" > /dev/null; then
    echo ""
    echo "✓ 检测到 sandbox 正在运行"
    echo ""
    echo "Agent jar 已更新！"
    echo "现在在 sandbox 中重新运行你的应用即可"
else
    echo ""
    echo "⚠ 未检测到运行中的 sandbox"
    echo ""
    echo "请先启动 sandbox："
    echo "  ./start-plugin-dev.sh"
    echo ""
    echo "或手动运行："
    echo "  ./gradlew runIde"
    echo "  然后运行此脚本更新 agent"
fi

echo ""
